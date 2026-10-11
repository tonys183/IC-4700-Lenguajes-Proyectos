leerEntrada(Negras, Blancas):- leerEntradaAux(0, 0, Negras, Blancas).
leerEntradaAux(NAcc, BAcc, NFin, BFin):- get_code(Char), convertChar(Char, NAcc, BAcc, NFin, BFin).

convertChar(10, N, B, N, B):-!.
convertChar(33, NIn, BIn, NOut, BOut):- NNext is NIn+1, leerEntradaAux(NNext, BIn, NOut, BOut).
convertChar(63, NIn, BIn, NOut, BOut):- BNext is BIn+1, leerEntradaAux(NIn, BNext, NOut, BOut).
convertChar(_, NIn, BIn, NOut, BOut):- leerEntradaAux(NIn, BIn, NOut, BOut).

split(L,N,LI,LD):- length(LI,N), append(LI,LD,L).

range(A, A, [A]):-!.
range(A, B, [A|R]):- A2 is A+1, range(A2, B, R).

comb(0, _, []):-!.
comb(N, [H|T], [H|R]):- N1 is N-1, comb(N1, T, R).
comb(N, [_|T], R):- comb(N, T, R).

findK(E,[E|T],K):- length([E|T], K).
findK(E,[_|T],K):- findK(E,T,K).

podable([[LastGuess, 0, _]|_PrevMoves], E, N):- findK(E, LastGuess, N), !.
podable([[_, _, _]|PrevMoves], E, N):- podable(PrevMoves,E,N), !.

permR(_, 0, [], _):-!.
permR(Colors, N, [E|P], PrevMoves):- depurarColores(Colors, PrevMoves, L), select(E, L, L1), \+podable(PrevMoves,E,N), append(L1,[E],L2), N1 is N-1 ,permR(L2, N1, P, PrevMoves).

permSimple([], []):-!.
permSimple(L, [X|R]):- select(X, L, L1), permSimple(L1, R).

repli(0,_L,_,[]):-!.
repli(N,L,K,R):- repli(N,L,K,K,R).
repli(0,_,_,_,[]):-!.
repli(N,[_|T],0,K,R):-!, repli(N,T,K,K,R).
repli(N,[H|T],E,K,[H|R]):- N1 is N-1, E2 is E-1, repli(N1,[H|T],E2,K,R).

crossLists([],L,L):-!.
crossLists(L,[],L):-!.
crossLists([H1|T1],[H2|T2],[H1,H2|C]):- crossLists(T1,T2,C).

diff(A,[],A):-!.
diff([],_B,[]):-!.
diff([H|T],B,R):- member(H,B), !, diff(T,B,R).
diff([H|T],B,[H|D]):- diff(T,B,D).

union([], L, L):-!.
union([H|T], L, R):- member(H, L), !, union(T, L, R).
union([H|T], L, [H|R]) :- union(T, L, R).

intersect(_, [], []):-!.
intersect([], _, []):-!.
intersect([H|T], L, [H|R]):- member(H, L), delete(T,H,T1), delete(L,H,L1), !, intersect(T1, L1, R).
intersect([_H|T], L, R):- intersect(T, L, R).

depurarColores(Colors, [], Colors) :- !.
depurarColores(Colors, [[Guess,0,0]|PrevMoves], NewColors) :- !, diff(Colors, Guess, C1),  depurarColores(C1, PrevMoves, NewColors).   
depurarColores(Colors, [_|PrevMoves], NewColors) :- depurarColores(Colors, PrevMoves, NewColors).

exactColors(N, [[LastGuess, Negras, Blancas]|_PrevMoves], LastGuess):- Total is Negras + Blancas, Total == N, length(LastGuess, N), !.
exactColors(N, [_|T], ExColors):- exactColors(N, T, ExColors).

allCodes(N, _Colors, PrevMoves, Code):- exactColors(N, PrevMoves, ExColors), permSimple(ExColors, Code), validarSol(Code, PrevMoves).
allCodes(N, Colors, PrevMoves, Code):-
    permR(Colors, N, Code, PrevMoves),
    validarSol(Code, PrevMoves).

contarNegras([],[],0):-!.
contarNegras([H|Sol],[H|Guess],N):- !, contarNegras(Sol,Guess,N1), N is N1+1.
contarNegras([_H1|Sol],[_H2|Guess], N):- contarNegras(Sol,Guess,N).

contarBlancas(_,[],0):-!.
contarBlancas(Sol, [H|Guess], B):- member(H,Sol), select(H, Sol, Sol1), !, contarBlancas(Sol1,Guess,B1), B is B1+1.
contarBlancas(Sol,[_|Guess],B):- contarBlancas(Sol,Guess,B).

validarSol(_GuessAct, []):-!.
validarSol(GuessAct, [[PrevGuess, Negras, Blancas]|T]):- 
    contarNegras(PrevGuess, GuessAct, Negras),
    Blancas1 is Blancas + Negras,
    contarBlancas(PrevGuess, GuessAct, Blancas1), !,
    validarSol(GuessAct, T).

rndSelect(_,0,[]):-!.
rndSelect(L,N,[E|T]):- N2 is N-1, length(L,LEN) ,random(1,LEN,K), findK(E,L,K), !, rndSelect(L,N2,T). 

buscarAleatorio(N, Colors, Moves, Guess):-
    rndSelect(Colors, N, Candidato),
    ( validarSol(Candidato, Moves) 
    -> Guess = Candidato
    ; buscarAleatorio(N, Colors, Moves, Guess)
    ).

numsUsed([], []):-!.
numsUsed([[Guess, _Negras, _Blancas]|PrevMoves], Nums):- numsUsed(PrevMoves, RestNums), union(Guess, RestNums, Nums).

actuState(N, _K,[[LastGuess, Negras, _Blancas]|_PrevMoves], normal, grupos(LastGuess, [])):- Negras is N-1, !.
actuState(N, Colors,[[LastGuess, Negras, Blancas]|PrevMoves], grupos(NiceGuess, _NUsados), afterGrupos(NiceGuess, LastGuess)):- 
    Total is Negras + Blancas, Total > 0,
    numsUsed(PrevMoves, TotalUse),
    intersect(TotalUse, Colors, Used),  
    diff(Colors, Used, Unused),
    length(Unused, LUnused),
    LUnused >= N, !.

actuState(_N, _K,[[LastGuess, _Negras, _Blancas]|_PrevMoves], grupos(NiceGuess, NUsados), grupos(NiceGuess, NewNums)):- append(NUsados, LastGuess, NewNums), !.
actuState(_N, _K,[[_LastGuess, _Negras, _Blancas]|_PrevMoves], afterGrupos(NiceGuess, LastLastGuess), afterGrupos(NiceGuess, LastLastGuess)):- !.
actuState(_N, _K,_PrevMoves, State, State):-!.

elegirGuess(N, Colors, Moves, Guess, 0, normal):- length(Colors, K), range(1,K,L), K =< 2*N, F is (2*N div K) + 1, repli(N,L,F,Guess), validarSol(Guess, Moves), !. 
elegirGuess(N, Colors, Moves, Guess, Try, normal):- Try < 3, depurarColores(Colors, Moves, NewColors), buscarAleatorio(N, NewColors, Moves, Guess), !.
elegirGuess(N, Colors, Moves, Guess, _Try, normal):- allCodes(N, Colors, Moves, Guess).
elegirGuess(N, Colors, Moves, Guess, _Try, grupos(NiceGuess, NumsUsados)):- !,
    numsUsed(Moves, Guessed1),
    union(Guessed1, NumsUsados, Guessed2),
    length(Colors, K),
    Mid is K // 2,
    split(Colors, Mid, L1, L2),
    reverse(L2, ReverL2),
    crossLists(ReverL2, L1, NumsIntercala),
    diff(NumsIntercala, Guessed2, NumsNoUsados),
    length(NumsNoUsados, L),
    (   L >= N
    ->  length(Guess, N),
        append(Guess, _, NumsNoUsados)
    ;   L > 0
    ->  N1 is N-1,
        comb(N1, NiceGuess, Combi),
        member(Missing, NumsNoUsados), 
        append(Combi, [Missing], PosiSol),
        permSimple(PosiSol, Guess),
        validarSol(Guess, Moves)
    ;   allCodes(N, Colors, Moves, Guess)
    ). 

elegirGuess(N, _Colors, Moves, Guess, _Try, afterGrupos(NiceGuess, LastGuess)):-
    N1 is N-1,
    comb(N1, NiceGuess, Combi),
    member(Missing, LastGuess), 
    append(Combi, [Missing], PosiSol),
    permSimple(PosiSol, Guess),
    validarSol(Guess, Moves), !.

checkRes(N, _, _Moves, _Try, N, _State):-!.
checkRes(N, Colors, Moves, Try, _Negras, State):-
    actuState(N, Colors, Moves, State, NewState),
    Try1 is Try+1,
    game(N, Colors, Moves, Try1, NewState).

game(N, Colors, Moves, Try, State):-
    elegirGuess(N, Colors, Moves, GuessAct, Try, State),
    format('~w~n', [GuessAct]),
    leerEntrada(Negras, Blancas),
    checkRes(N, Colors, [[GuessAct, Negras, Blancas]|Moves], Try, Negras, State).

solve(N,K):- 
    randomize, 
    range(1, K, Colors),
    length(Colors, KT),
    Long is KT // 2,
    split(Colors, Long, C1, C2),
    reverse(C1, C1R),
    crossLists(C2, C1R, NewColors),
    game(N, NewColors, [], 0, normal).