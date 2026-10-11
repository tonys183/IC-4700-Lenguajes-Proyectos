// Autores: Sebastián Castillo, Fiorella González, Saúl Pacheco, Gael Ruíz, Anthony Segura.
// Evaluador de manos para las diferentes modalidades de HORSE.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int max(int a, int b) { return (a > b)? a : b; }
int min(int a, int b) { return (a < b)? a : b; }

typedef enum {
    CLUBS, DIAMONDS, HEARTS, SPADES
} Suit;

typedef enum {
    RANK_2 = 2, RANK_3 = 3, RANK_4 = 4, RANK_5 = 5, RANK_6 = 6,
    RANK_7 = 7, RANK_8 = 8, RANK_9 = 9, RANK_10 = 10,
    RANK_J = 11, RANK_Q = 12, RANK_K = 13, RANK_A = 14
} Rank;

typedef struct {
    Rank rank;
    Suit suit;
} Card;

typedef enum {
    HOLDEM,
    OMAHA_HI,
    RAZZ,
    STUD,
    OMAHA_HILO
} GameVariant;

typedef struct {
    int score_hi;
    int score_lo;
} HandScore;

// para usar qsort descendiente
int descendingComp(const void *a, const void *b) {
    const Card *ca = a;
    const Card *cb = b;
    if (ca->rank < cb->rank) return 1;
    if (ca->rank > cb->rank) return -1;
    return 0;
}

// para usar qsort descendiente clavando ace al final
int descendingCompAce1(const void *a, const void *b) {
    const Card *ca = a;
    const Card *cb = b;
    if(ca->rank == 14) return 1;
    if(cb->rank == 14) return -1;
    if (ca->rank < cb->rank) return 1;
    if (ca->rank > cb->rank) return -1;
    return 0;
}

// -------- evaluacion de mano 

// tupla
typedef struct {
    int a1;
    int a2;
    int a3;
    int a4;
    int a5;
} Tiebreak;

int binPow(int base, int exp){
    if(exp == 0) return 1;
    int res = binPow(base, exp >> 1);
    res *= res;
    if(exp % 2) res*=base;
    return res;
}

int calculateTiebreak(Tiebreak tiebreak, int base){
    int value = 0;
    if(tiebreak.a1 != -1) value += tiebreak.a1 * binPow(base, 4);
    if(tiebreak.a2 != -1) value += tiebreak.a2 * binPow(base, 3);
    if(tiebreak.a3 != -1) value += tiebreak.a3 * binPow(base, 2);
    if(tiebreak.a4 != -1) value += tiebreak.a4 * binPow(base, 1);
    if(tiebreak.a5 != -1) value += tiebreak.a5 * binPow(base, 0);
    return value;
}

/*

CADA UNA DE ESTAS FUNCIONES PONDERA UNA MANO Y ASIGNA UN SCORE SI CUMPLE LA CONDICIÓN DE LA JERARQUÍA, EN OTRO CASO RETORNA 0
La idea del score es asignarle a una mano un número en base 15, el most significant digit es el value (posición en la jerarquía) de la combinación (mayor es mejor), 
los digitos de indice 0 a 4 son usados para desempates, estos dependen de cada nivel en la jerarquía


Para los score low basta calcular four of a kind, full house, two pair, etc y entre mejores sean estos peores son para low, solo que hay que tratar ace como 1 en cada una
por eso algunas funciones tienen lowAce de parametro.

*/


int royalFlushScore(Card sortedCards[5], int base, int k, int value) {
    int isRoyalFlush = 1;
    if(sortedCards[0].rank == 14) {
        for(int i = 0; i < 4; i++){
            if(sortedCards[i].suit != sortedCards[i+1].suit || sortedCards[i+1].rank != sortedCards[i].rank-1) {
                isRoyalFlush = 0;
                break;
            }
        } 
    } else isRoyalFlush = 0;
    return (isRoyalFlush)? value * binPow(base, k) : 0;
}

int straightFlushScore(Card sortedCards[5], int base, int k, int value) {
    int isWheel = 1;
    int wheel[5] = {14, 5, 4, 3, 2};
    for (int i = 0; i < 5; ++i) {
        if(i < 4){
            if(sortedCards[i].suit != sortedCards[i+1].suit) {
                isWheel = 0;
                break;
            }
        }
        if (sortedCards[i].rank != wheel[i]) {
            isWheel = 0;
            break;
        }
    }

    int isStraightFlush = isWheel;
    if(!isWheel){
        isStraightFlush = 1;
        for(int i = 0; i < 4; i++){
        if(sortedCards[i].suit != sortedCards[i+1].suit || sortedCards[i+1].rank != sortedCards[i].rank-1) {
            isStraightFlush = 0;
            break;
            }
        }
    }
    
    int topCard = (isWheel) ? 5 : sortedCards[0].rank;
    Tiebreak tb = { topCard, -1, -1, -1, -1 };
    return (isStraightFlush)? value * binPow(base, k) + calculateTiebreak(tb, base) : 0;
}

int fourOfAKindScore(Card sortedCards[5], int base, int k, int value, int lowAce){
    int isFourOfAKind = 1;
    int freq[15] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    for(int i = 0; i < 5; i++) {
        freq[sortedCards[i].rank]++;
    }
    int one = 0, four = 0;
    for(int i = 1; i <= 14; i++) {
        if(freq[i] == 1) one = i;
        else if (freq[i] == 4) four = i;
    }
    if(four == 14 && lowAce) four = 1;
    if(one == 14 && lowAce) one = 1;
    if(!one || !four) isFourOfAKind = 0;
    Tiebreak tb = { four, one, -1, -1, -1 };
    return (isFourOfAKind)? value * binPow(base, k) + calculateTiebreak(tb, base) : 0;
}

int fullHouseScore(Card sortedCards[5], int base, int k, int value, int lowAce){
    int isFullHouse = 1;
    int freq[15] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    for(int i = 0; i < 5; i++) {
        freq[sortedCards[i].rank]++;
    }
    int two = 0, three = 0;
    for(int i = 1; i <= 14; i++) {
        if(freq[i] == 2) two = i;
        else if (freq[i] == 3) three = i;
    }
    if(two == 14 && lowAce) two = 1;
    if(three == 14 && lowAce) three = 1;
    if(!two || !three) isFullHouse = 0;
    Tiebreak tb = { three, two, -1, -1, -1 };
    return (isFullHouse)? value * binPow(base, k) + calculateTiebreak(tb, base) : 0;
}

int flushScore(Card sortedCards[5], int base, int k, int value){
    int isFlush = 1;
    for(int i = 0; i < 4; i++) {
        if(sortedCards[i].suit != sortedCards[i+1].suit) {
            isFlush = 0;
            break;
        }
    }
    
    Tiebreak tb = { sortedCards[0].rank, sortedCards[1].rank, sortedCards[2].rank, sortedCards[3].rank, sortedCards[4].rank };
    return (isFlush)? value * binPow(base, k) + calculateTiebreak(tb, base) : 0;
}

int straightScore(Card sortedCards[5], int base, int k, int value){
    int isWheel = 1;
    int wheel[5] = {14, 5, 4, 3, 2};
    for (int i = 0; i < 5; ++i) {
        if (sortedCards[i].rank != wheel[i]) {
            isWheel = 0;
            break;
        }
    }

    int isStraight = isWheel;
    if (!isWheel) {
        isStraight = 1;
        for (int i = 0; i < 4; ++i) {
            if (sortedCards[i].rank != sortedCards[i+1].rank + 1) {
                isStraight = 0;
                break;
            }
        }
    }

    Tiebreak tb = { -1, -1, -1, -1, -1 };
    tb.a1 = (isWheel)? 5 : sortedCards[0].rank;
    return (isStraight)? value * binPow(base, k) + calculateTiebreak(tb, base) : 0;
}


int threeOfAKindScore(Card sortedCards[5], int base, int k, int value, int lowAce){
    int isThreeOfAKind = 1;
    int freq[15] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    for(int i = 0; i < 5; i++) {
        freq[sortedCards[i].rank]++;
    }
    int kick1 = 0, kick2 = 0, three = 0;
    for(int i = 1; i <= 14; i++) {
        if(freq[i] == 3) three = i;
        else if (freq[i] == 1 && kick1 == 0) kick1 = i;
        else if (freq[i] == 1 && kick1 != 0) kick2 = i;
    }
    if(!three || !kick1 || !kick2) isThreeOfAKind = 0;
    if(three == 14 && lowAce) three = 1;
    if(kick1 == 14 && lowAce) kick1 = 1;
    if(kick2 == 14 && lowAce) kick2 = 1;
    Tiebreak tb = { three, max(kick1, kick2), min(kick1, kick2), -1, -1 };
    return (isThreeOfAKind)? value * binPow(base, k) + calculateTiebreak(tb, base) : 0;
}

int twoPairScore(Card sortedCards[5], int base, int k, int value, int lowAce){
    int isTwoPair = 1;
    int freq[15] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    for(int i = 0; i < 5; i++) {
        freq[sortedCards[i].rank]++;
    }
    int kick = 0, two1 = 0, two2 = 0;
    for(int i = 1; i <= 14; i++) {
        if(freq[i] == 2 && two1 == 0) two1 = i;
        else if (freq[i] == 2 && two1 != 0) two2 = i;
        else if (freq[i] == 1) kick = i;
    }
    if(!two1 || !two2 || !kick) isTwoPair = 0;
    if(two1 == 14 && lowAce) two1 = 1;
    if(two2 == 14 && lowAce) two2 = 1;
    if(kick == 14 && lowAce) kick =1;
    Tiebreak tb = { max(two1, two2), min(two1, two2), kick, -1, -1};
    return (isTwoPair)? value * binPow(base, k) + calculateTiebreak(tb, base) : 0;
}

int pairScore(Card sortedCards[5], int base, int k, int value, int lowAce){
    int isPair = 1;
    int freq[15] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    for(int i = 0; i < 5; i++) {
        freq[sortedCards[i].rank]++;
    }
    int two = 0, kick1 = 0, kick2 = 0, kick3 = 0;
    for(int i = 1; i <= 14; i++) {
        if(freq[i] == 2) two = i;
        else if (freq[i] == 1 && kick1 == 0) kick1 = i;
        else if (freq[i] == 1 && kick1 != 0) kick2 = i;
        else if (freq[i] == 1 && kick2 != 0) kick3 = i;
    }
    if(!two || !kick1 || !kick2 || !kick3) isPair = 0;
    if (two == 14 && lowAce) two = 1;
    if(kick1 == 14 && lowAce) kick1 = 1;
    if(kick2 == 14 && lowAce) kick2 = 1;
    if(kick3 == 14 && lowAce) kick3 = 1;
    int highKick = max(kick1, max(kick2, kick3));
    int minKick = min(kick1, min(kick2, kick3));
    int middleKick = kick1 + kick2 + kick3 - highKick - minKick;
    Tiebreak tb = { two, highKick, middleKick, minKick, -1};
    return (isPair)? value * binPow(base, k) + calculateTiebreak(tb, base) : 0;
}

int highCard(Card sortedCards[5], int base, int k, int value, int lowAce) {
    int first = (lowAce && sortedCards[0].rank == 14)? 1 : sortedCards[0].rank;
    int second = (lowAce && sortedCards[1].rank == 14)? 1 : sortedCards[1].rank;
    int third = (lowAce && sortedCards[2].rank == 14)? 1 : sortedCards[2].rank;
    int fourth = (lowAce && sortedCards[3].rank == 14)? 1 : sortedCards[3].rank;
    int fifth = (lowAce && sortedCards[4].rank == 14)? 1 : sortedCards[4].rank;
    Tiebreak tb = {first, second, third, fourth, fifth};
    return value * binPow(base, k) + calculateTiebreak(tb, base);
}

// solo para buscar si se pasa de ocho en omaha lo
int maxInHand(Card cards[5], int lowAce) {
    int maxi = -1;
    for(int i = 0; i < 5; i++) {
        int cardValue = (cards[i].rank == 14 && lowAce)? 1 : cards[i].rank;
        maxi = max(maxi, cardValue);
    } 
    return maxi;
}

// retorna {score, -1}
HandScore eval5CardHandHigh(Card cards[5]) {
    int score = 0, base = 15, k = 5, value = 10;
    qsort(cards, 5, sizeof(Card), descendingComp);
    score = royalFlushScore(cards, base, k, value--);
    if(!score) score = straightFlushScore(cards, base, k, value--);
    if(!score) score = fourOfAKindScore(cards, base, k, value--, 0);
    if(!score) score = fullHouseScore(cards, base, k, value--, 0);
    if(!score) score = flushScore(cards, base, k, value--);
    if(!score) score = straightScore(cards, base, k, value--);
    if(!score) score = threeOfAKindScore(cards, base, k, value--, 0);
    if(!score) score = twoPairScore(cards, base, k, value--, 0);
    if(!score) score = pairScore(cards, base, k, value--, 0);
    if(!score) score = highCard(cards, base, k, value--, 0);
    HandScore hs = {score, -1};
    return hs;
}

// retorna {-1, score}
HandScore eval5CardHandLow(Card cards[], GameVariant gameVariant) { // si no califica en eight or better se devuelve {-1, -1}
    qsort(cards, 5, sizeof(Card), descendingCompAce1);
    int top = binPow(15, 6); // solo para que score más grande sea mejor
    int badScore = 0, base = 15, k = 5, value = 6;
    badScore = fourOfAKindScore(cards, base, k, value--, 1);
    if(!badScore) badScore = fullHouseScore(cards, base, k, value--, 1);
    if(!badScore) badScore = threeOfAKindScore(cards, base, k, value--, 1);
    if(!badScore) badScore = twoPairScore(cards, base, k, value--, 1);
    if(!badScore) badScore = pairScore(cards, base, k, value--, 1);

    if(gameVariant == OMAHA_HILO) {
        HandScore hs = {-1, -1};
        if(badScore) {
            return hs;
        } else {
            int maxi = maxInHand(cards, 1);
            if (maxi > 8) return hs;
        }
    }

    if(!badScore) badScore = highCard(cards, base, k, value--, 1);
    HandScore score = {-1, top-badScore};
    return score;
}

// evaluador final para cualquier mano de 5 cartas incluyendo juego
HandScore eval5CardHand(Card cards[5], GameVariant gameVariant) { 
    if(gameVariant == OMAHA_HILO){
        HandScore hsHi = eval5CardHandHigh(cards);
        HandScore hsLo = eval5CardHandLow(cards, gameVariant);
        HandScore finalHs = {hsHi.score_hi, hsLo.score_lo};
        return finalHs;
    }
    else if(gameVariant == RAZZ) {
        return eval5CardHandLow(cards, gameVariant);
    } else return eval5CardHandHigh(cards);
}

HandScore bestHandScorePossibility(Card playerCards[], Card communityCards[], int pCards, int cCards, GameVariant gameVariant) {
    HandScore best;
    best.score_hi = -1;
    best.score_lo = -1;

    if (gameVariant == HOLDEM || gameVariant == STUD || gameVariant == RAZZ) {
        int tot = pCards + cCards;
        if (tot < 5) return best;

        Card cardPool[tot];
        for (int i = 0; i < pCards; ++i)      cardPool[i]       = playerCards[i];
        for (int i = 0; i < cCards; ++i)      cardPool[pCards + i] = communityCards[i];

        Card comb[5];

        for (int a = 0; a < tot - 4; ++a)
        for (int b = a + 1; b < tot - 3; ++b)
        for (int c = b + 1; c < tot - 2; ++c)
        for (int d = c + 1; d < tot - 1; ++d)
        for (int e = d + 1; e < tot;       ++e) {

            comb[0] = cardPool[a];
            comb[1] = cardPool[b];
            comb[2] = cardPool[c];
            comb[3] = cardPool[d];
            comb[4] = cardPool[e];

            HandScore hs = eval5CardHand(comb, gameVariant);

            if (gameVariant == RAZZ) {
                if (hs.score_lo > best.score_lo)
                    best.score_lo = hs.score_lo;
            } else {
                if (hs.score_hi > best.score_hi)
                    best.score_hi = hs.score_hi;
            }
        }
    }
    else { 
        if (pCards < 4 || cCards < 3) return best;

        Card comb1[2];
        Card comb2[3];

        for (int a = 0; a < pCards - 1; ++a)
        for (int b = a + 1; b < pCards;   ++b)
        for (int c = 0; c < cCards - 2; ++c)
        for (int d = c + 1; d < cCards - 1; ++d)
        for (int e = d + 1; e < cCards;   ++e) {

            comb1[0] = playerCards[a];
            comb1[1] = playerCards[b];

            comb2[0] = communityCards[c];
            comb2[1] = communityCards[d];
            comb2[2] = communityCards[e];

            Card fullHand[5];
            fullHand[0] = comb1[0];
            fullHand[1] = comb1[1];
            fullHand[2] = comb2[0];
            fullHand[3] = comb2[1];
            fullHand[4] = comb2[2];

            HandScore hs = eval5CardHand(fullHand, gameVariant);

            if (hs.score_hi > best.score_hi)
                best.score_hi = hs.score_hi;
            if (hs.score_lo > best.score_lo)
                best.score_lo = hs.score_lo;
        }
    }

    return best;
}




// sacar ganador de una lista de resultados


// guarda los resultados potenciales de cada jugador en results
void evaluateTablePotential(GameVariant gameVariant, Card ** players, int numPlayers, int numPlayerCards, Card board[], int numBoard, HandScore results[]) {
    for(int i = 0; i < numPlayers; i++){
        Card playerCards[numPlayerCards];
        for(int j = 0; j < numPlayerCards; j++) playerCards[j] = players[i][j];
        HandScore hs = bestHandScorePossibility(playerCards, board, numPlayerCards, numBoard, gameVariant);
        results[i] = hs;
    }
}

// guarda los resultados de la mano que FUE JUGADA por cada jugador en results -- NO SÉ SI OCUPAN ESTO PERO AQUÍ ESTÁ IGUAL :>D
void evaluateTable(GameVariant gameVariant, Card ** players, int numPlayers, int numPlayerCards, HandScore results[]) {
    for(int i = 0; i < numPlayers; i++) {
        Card playerCards[numPlayerCards];
        for(int j = 0; j < numPlayerCards; j++) playerCards[j] = players[i][j];
        if(gameVariant == RAZZ) {
            Card *no = NULL;
            results[i] = bestHandScorePossibility(playerCards, no, numPlayerCards, 0, RAZZ);
        }       
        else results[i] = eval5CardHand(playerCards, gameVariant);
    }
}
 
// retorna la cantidad de ganadores y los guarda en winners ALOCAR WINNERS ANTES DE LLAMAR CON TAMAÑO NUM_PLAYERS !!!!!
int findHighWinner(HandScore results[], int num_players, int * winners) {
    int maxScore = results[0].score_hi;
    int curWinIdx = 0;
    winners[0] = 0;

    for(int i = 1; i < num_players; i++) {
        if(results[i].score_hi > maxScore) {
            maxScore = results[i].score_hi;
            curWinIdx = 0;
            winners[0] = i;
        } else if(results[i].score_hi == maxScore) {
            curWinIdx++;
            winners[curWinIdx] = i;
        }
    }

    for(int i = curWinIdx + 1; i < num_players; i++) winners[i] = -1;

    return curWinIdx + 1;
}

int findLowWinner(HandScore results[], int num_players, int * winners, GameVariant gv) {
    if(gv == HOLDEM || gv == OMAHA_HI || gv == STUD) return 0;
    int maxScore = results[0].score_lo;
    int curWinIdx = 0;
    winners[0] = 0;

    for(int i = 1; i < num_players; i++) {
        if(results[i].score_lo > maxScore) {
            maxScore = results[i].score_lo;
            curWinIdx = 0;
            winners[0] = i;
        } else if(results[i].score_lo == maxScore) {
            curWinIdx++;
            winners[curWinIdx] = i;
        }
    }
    for(int i = curWinIdx + 1; i < num_players; i++) winners[i] = -1;

    return curWinIdx + 1;
}


// int main() {

//     GameVariant game = OMAHA_HILO;

//     int numPlayers = 3;
//     int numPlayerCards = 4;
//     int numCommunity = 5;

//     Card board[5] = {
//         { RANK_5, HEARTS },
//         { RANK_6, SPADES },
//         { RANK_7, DIAMONDS },
//         { RANK_8, CLUBS },
//         { RANK_K, HEARTS }
//     };

//     Card p1[4] = {
//         { RANK_2, CLUBS },
//         { RANK_3, DIAMONDS },
//         { RANK_9, SPADES },
//         { RANK_Q, HEARTS }
//     };

//     Card p2[4] = {
//         { RANK_A, HEARTS },
//         { RANK_4, SPADES },
//         { RANK_J, CLUBS },
//         { RANK_K, DIAMONDS }
//     };

//     Card p3[4] = {
//         { RANK_8, HEARTS },
//         { RANK_6, CLUBS },
//         { RANK_2, SPADES },
//         { RANK_4, HEARTS }
//     };

//     Card *players[3];
//     players[0] = p1;
//     players[1] = p2;
//     players[2] = p3;

//     HandScore results[3];

//     evaluateTablePotential(game,
//                            players,
//                            numPlayers,
//                            numPlayerCards,
//                            board,
//                            numCommunity,
//                            results);

//     int hi[3], lo[3];

//     for (int i = 0; i < numPlayers; i++) {
//         printf("Player %d: HI=%d LOW=%d\n",
//                i+1,
//                results[i].score_hi,
//                results[i].score_lo);
//     }

//     int hiCount = findHighWinner(results, numPlayers, hi);
//     int loCount = findLowWinner(results, numPlayers, lo);

//     printf("\nHI WINNER(S):\n");
//     for (int i = 0; i < hiCount; i++)
//         printf("Player %d\n", hi[i] + 1);

//     printf("\nLOW WINNER(S):\n");
//     for (int i = 0; i < loCount; i++)
//         printf("Player %d\n", lo[i] + 1);

//     return 0;
// }


/*

uso sería como 
HandScore results[x];                                                                           vvvvv
char ** names;
evaluateTable(gameVariant, players, numPlayers, numPlayerCards, board, numBoardCards, results);
int * highWinnerIdxs = malloc(num_players * sizeof(int));
int * lowWinnerIdxs = malloc(num_players * sizeof(int));
int highWinnerAmount = findHighWinner(results, num_players, highWinnerIdxs);
int lowWinnerIdxAmount = findLowWinner(results, num_players, lowWinnerIdxs);


*/
