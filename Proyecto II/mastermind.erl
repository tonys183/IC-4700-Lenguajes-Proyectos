-module(mastermind).
-export([start/2]).

leerEntrada() ->
    Input = io:get_line("" ),
    {ok, Tokens, _} = erl_scan:string(Input ++ "."),
    {ok, Expr} = erl_parse:parse_term(Tokens), 
    Expr.

generarCodigo(N, K) ->  
    _ = rand:seed(default),
    [rand:uniform(K) || _ <- lists:seq(1, N)].

start(N,K) -> game(generarCodigo(N, K), leerEntrada()).

game(Res, Intento) when Res =:= Intento -> io:format("~nGanó~n");
game(Res, Intento) -> Negras = contarNegras(Res, Intento), 
                           Blancas = contarBlancas(Res, Intento) - Negras,
                           io:format("\"~s\"~n", [lists:duplicate(Negras, $!) ++ lists:duplicate(Blancas, $?)]),
                           game(Res, leerEntrada()).

contarNegras([], []) -> 0;
contarNegras([H|TRes], [H|TIntento]) -> 1 + contarNegras(TRes, TIntento);
contarNegras([_|TRes], [_|TIntento])-> contarNegras(TRes, TIntento).  

contarBlancas(_Res, []) -> 0;
contarBlancas(Res, [H|TIntento]) ->
    case lists:member(H, Res) of
        true -> 1 + contarBlancas(lists:delete(H, Res), TIntento);
        false -> contarBlancas(Res, TIntento) 
    end.