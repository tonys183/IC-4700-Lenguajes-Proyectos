#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#include "HandEvaluator.c"


typedef struct {
    char name[64];
    Card *cards;
    int numCards;
} Player;

Suit parseSuit(char c) {
    switch (c) {
        case 'C': return CLUBS;
        case 'D': return DIAMONDS;
        case 'H': return HEARTS;
        case 'S': return SPADES;
        default:
            return CLUBS;
    }
}

Rank parseRank(int n) {
    if (n >= 2 && n <= 13) return (Rank)n;
    return RANK_A;
}

int parseCommunityCards(char *text, Card community[]) {
    int count = 0;

    char *ptr = strstr(text, "COMUNITY_CARDS:");
    if (!ptr) {
        return 0;
    }

    ptr += strlen("COMUNITY_CARDS:");
    char *tok = strtok(ptr, " ");

    while (tok) {

        if (strlen(tok) >= 3 && tok[1] == ',') {
            community[count].suit = parseSuit(tok[0]);
            community[count].rank = parseRank(atoi(tok + 2));
            count++;
        }
        tok = strtok(NULL, " ");
    }

    return count;
}

int parseCommunityCardsSafe(const char *text, Card community[]) {
    char *copy = strdup(text);
    int n = parseCommunityCards(copy, community);
    free(copy);
    return n;
}


GameVariant parseGameVariant(const char *text) {
    char line[64];
    int i = 0;

    while (text[i] != '\n' && text[i] && i < 63) {
        line[i] = text[i];
        i++;
    }
    line[i] = '\0';


    if (!strcmp(line, "HOLDEM")) return HOLDEM;
    if (!strcmp(line, "OMAHA")) return OMAHA_HI;
    if (!strcmp(line, "OMAHA_HILO")) return OMAHA_HILO;
    if (!strcmp(line, "RAZZ")) return RAZZ;
    if (!strcmp(line, "STUD")) return STUD;

    return HOLDEM;
}


int parsePlayers(const char *buf, Player **playersOut, int *numPlayersOut) {

    char *copy = strdup(buf);
    int cap = 4;
    int count = 0;

    Player *players = malloc(cap * sizeof(Player));

    char *p = strchr(copy, '\n');
    if (!p) return -1;
    p++;

    while (*p) {

        char *line = p;
        char *end = strchr(p, '\n');

        if (end) {
            *end = '\0';
            p = end + 1;
        } else {
            p += strlen(p);
        }

        if (*line == '\0') continue;
        if (!strncmp(line, "COMUNITY_CARDS:", 15)) break;


        char *colon = strchr(line, ':');
        if (!colon) {
            continue;
        }

        if (count == cap) {
            cap *= 2;
            players = realloc(players, cap * sizeof(Player));
        }

        *colon = '\0';
        strncpy(players[count].name, line, 63);
        players[count].name[63] = '\0';

        char *cardsText = colon + 1;
        players[count].cards = malloc(10 * sizeof(Card));
        players[count].numCards = 0;


        char *tok = strtok(cardsText, " ");
        while (tok) {


            if (strlen(tok) >= 3 && tok[1] == ',') {
                players[count].cards[players[count].numCards].suit = parseSuit(tok[0]);
                players[count].cards[players[count].numCards].rank = parseRank(atoi(tok + 2));


                players[count].numCards++;
            }
            tok = strtok(NULL, " ");
        }


        count++;
    }

    free(copy);


    *playersOut = players;
    *numPlayersOut = count;

    return 0;
}

int main() {

    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        perror("socket");
        return 1;
    }

    struct sockaddr_in addr;
    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_port = htons(8081);

    if (inet_pton(AF_INET, "100.85.82.49", &addr.sin_addr) <= 0) {
        perror("inet_pton");
        return 1;
    }

    printf("Conectando al servidor Java...\n");

    if (connect(sock, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
        perror("connect");
        return 1;
    }

    printf("Conectado al servidor Java en 8081\n");

    while (1) {
        char buffer[4096];

        int len = read(sock, buffer, sizeof(buffer) - 1);
        if (len <= 0) {
            printf("Servidor Java desconectado.\n");
            break;
        }

        buffer[len] = '\0';

        GameVariant gameVariant = parseGameVariant(buffer);

        Player *players = NULL;
        int numPlayers = 0;
        parsePlayers(buffer, &players, &numPlayers);

        Card community[7];
        int numCommunityCards = parseCommunityCardsSafe(buffer, community);


        Card **playerCards = malloc(numPlayers * sizeof(Card *));
        int numPlayerCards = players[0].numCards;

        for (int i = 0; i < numPlayers; i++)
            playerCards[i] = players[i].cards;

        HandScore *results = malloc(numPlayers * sizeof(HandScore));

        evaluateTablePotential(
            gameVariant,
            playerCards,
            numPlayers,
            numPlayerCards,
            community,
            numCommunityCards,
            results
        );


        int hiIdx[32], loIdx[32];
        int hiCount = findHighWinner(results, numPlayers, hiIdx);
        int loCount = findLowWinner(results, numPlayers, loIdx, gameVariant);


        char response[2048] = {0};

        strcat(response, "WINNERS_HI:\n");
        for (int i = 0; i < hiCount; i++) {
            strcat(response, players[hiIdx[i]].name);
            strcat(response, "\n");
        }

        strcat(response, "\nWINNERS_LOW:\n");
        for (int i = 0; i < loCount; i++) {
            strcat(response, players[loIdx[i]].name);
            strcat(response, "\n");
        }

        strcat(response, "<END>\n");

        printf("\n=== RESPONSE ===\n%s\n", response);

        write(sock, response, strlen(response));

        for (int i = 0; i < numPlayers; i++)
            free(players[i].cards);

        free(players);
        free(playerCards);
        free(results);
    }

    close(sock);
    return 0;
}
