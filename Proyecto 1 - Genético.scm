#lang racket
(require plot racket/gui/base)

;==== FUNCIONES AUXILIARES ====;
;--- Altura ---;
; Dominio: Un árbol.
; Codominio: Un entero (altura del árbol).
(define altura
  (lambda (A)
    (cond
      ((null? A) -1)
      ((not (list? A)) 0)
      (else (+ 1 (max (altura (cadr A)) (altura (caddr A))))))))

;--- Nivel ---;
; Dominio: Un árbol y un entero n (nivel).
; Codominio: Un conjunto de nodos.
(define nivel
  (lambda (A n)
    (cond
      ((null? A) A)
      ((zero? n) (list A))
      ((not (list? A)) (list))
      ((list? A) (append (nivel (cadr A) (- n 1)) (nivel (caddr A) (- n 1))))
      (else (list)))))

;--- Elemento en Índice ---;
; Dominio: Una lista y un entero n.
; Codominio: Un valor de la lista.
(define elementoEn
  (lambda (L n)
    (cond
      ((zero? n) (car L))
      (else (elementoEn (cdr L) (- n 1))))))

;--- Pop ---;
; Dominio: Una lista.
; Codominio: Una lista sin el último elemento.
(define pop
  (lambda (L)
    (cond
      ((or (null? (cdr L)) (null? L) ) '())
      (else (cons (car L) (pop (cdr L)) )))))

;--- Top ---;
; Dominio: Una lista.
; Codominio: El último valor de la lista.
(define top
  (lambda (L)
    (cond
      ((null? (cdr L)) (car L))
      (else (top (cdr L) )))))

;--- Primeros-n ---;
; Dominio: Una lista y un entero n.
; Codominio: Una lista con los n primeros elementos.
(define primeros-n
  (lambda (L n)
    (cond
      ((or (null? L) (zero? n)) '())
      (else (cons (car L) (primeros-n (cdr L) (- n 1)))))))

;--- Quitar Elemento ---;
; Dominio: Una lista y un entero n.
; Codominio: Una lista sin el enésimo valor.
(define quitarElemento
  (lambda (L n)
    (cond
      ((null? L) L)
      ((zero? n) (cdr L))
      (else (cons (car L) (quitarElemento (cdr L) (- n 1))) )
    )))

;--- Presición k ---;
; Dominio: Dos enteros, n y k.
; Codominio: Un valor real con k o menos decimales y parte entera n.
(define precisionK
  (lambda (n k)
   (/ (round (* n (expt 10 k))) (expt 10 k))))

;--- Real Random ---;
; Dominio: Vacio.
; Codominio: Un valor real.
(define realRand
  (lambda ()
    (cond
    ((< (random) 0.5) (precisionK (* -1 (* (random) (expt 10 (random 2)))) 6))
    (else (precisionK (* (random) (expt 10 (random 2))) 6)))))

;--- Real Random Positivo ---;
; Dominio: Vacio.
; Codominio: Un valor real no negativo.
(define realPositiveRand
  (lambda ()
    (precisionK (* (random) (expt 10 (random 2))) 6)))


;==== OPERACIONES ====;
;--- División válida ---;
; Dominio: Dos números reales a y b.
; Codominio: El cociente a/b o 'error si la división no es válida.
(define divValida
  (lambda (a b)
    (cond
      ((or (eq? 'error a) (eq? 'error b)) 'error)
      ((= b 0) 'error)
      (else (/ a b)))))

;--- Exp válida ---;
; Dominio: Dos números reales b (base) y e (exponente).
; Codominio: El resultado de b^e o 'error si la operación no es válida.
(define expValida
  (lambda (b e)
    (cond
      ((or (eq? 'error e) (eq? 'error b)) 'error)
      ((and (zero? b) (< e 0)) 'error)
      ((and (< b 0) (not (integer? e))) (expt (abs b) e))
      ((> (abs e) 20) (expt b (random 10)))
      ((> (abs b) 1000) (expt (+ 1 (random 100)) e))
      (else (expt b e)))))

;--- Log válido ---;
; Dominio: Dos números reales a y b.
; Codominio: El logaritmo de a en base b o 'error si no es válido.
(define logValido
  (lambda (a b)
    (cond
      ((or (eq? 'error a) (eq? 'error b)) 'error)
      ((or (<= a 0) (<= b 0) (= b 1)) 'error)
      (else (divValida (log a) (log b))))))

;--- Suma válida ---;
; Dominio: Dos números reales a y b.
; Codominio: La suma a+b o 'error si no es válida.
(define sumaValida
  (lambda (a b)
      (cond
        ((or (eq? 'error a) (eq? 'error b)) 'error)
        ((or (eq? a -inf.0) (eq? b -inf.0) (eq? a +inf.0) (eq? b +inf.0) ) 'error)
        (else (+ a b) 
    ))))

;--- Resta válida ---;
; Dominio: Dos números reales a y b.
; Codominio: La resta a-b o 'error si no es válida.
(define restaValida
  (lambda (a b)
      (cond
        ((or (eq? 'error a) (eq? 'error b)) 'error)
        ((or (eq? a -inf.0) (eq? b -inf.0) (eq? a +inf.0) (eq? b +inf.0) ) 'error)
        (else (- a b) 
    ))))

;--- Multiplicación válida ---;
; Dominio: Dos números reales a y b.
; Codominio: El producto a*b o 'error si no es válido.
(define mulValida
  (lambda (a b)
      (cond
        ((or (eq? 'error a) (eq? 'error b)) 'error)
        ((or (eq? a -inf.0) (eq? b -inf.0) (eq? a +inf.0) (eq? b +inf.0) ) 'error)
        (else (* a b) 
    ))))
  
;--- Definición de Operadores ---;
(define opsValidas (list
                    (cons '+ sumaValida)
                    (cons '- restaValida)
                    (cons '* mulValida)
                    (cons '/ divValida)
                    (cons 'loga_b logValido)
                    (cons '** expValida)))

;--- Operaciones Posibles ---;
(define opsPosibles (map (lambda(i) (cdr i)) opsValidas)) ;Lista de funciones

;--- Cota de error ---;
(define COTA_ERROR 1.0e7)


;==== SET ====;
;--- Crear Set ---;
; Dominio: Vacio.
; Codominio: Un conjunto vacío (representado como lista).
(define crearSet
  (lambda () (list)))

;--- Insertar Set ---;
; Dominio: Un conjunto S y un elemento x.
; Codominio: El conjunto S con x insertado (sin duplicados).
(define insertarSet
  (lambda (S x)
    (cond
      ((member x S equal?) S)
      (else (cons x S)))))


;==== MANEJO DE INDIVIDUOS ====;
;--- Eval operación aux ---;
; Dominio: Un valor resultante de una operación.
; Codominio: Un número real o COTA_ERROR.
(define evalOps-aux
  (lambda (result)
    (cond
      ((eq? result 'error) COTA_ERROR)
      ((equal? result +inf.0) COTA_ERROR)
      ((equal? result -inf.0) COTA_ERROR)
      (else result))))

;--- Eval operación ---;
; Dominio: Una lista de parámetros paramOp y una operación op.
; Codominio: El resultado de aplicar op a paramOp o COTA_ERROR.
(define evalOps
  (lambda (paramOp op)
    (cond
      ((member 'error paramOp equal?) COTA_ERROR)
      ((member +inf.0 paramOp equal?) COTA_ERROR)
      ((member -inf.0 paramOp equal?) COTA_ERROR)
      (else (evalOps-aux (apply op paramOp))))))

;--- Eval árbol ---;
; Dominio: Un árbol de operaciones y dos números reales x e y.
; Codominio: Un número real resultante de evaluar el árbol.
(define evalArbol
  (lambda (arbol x y)
    (cond
      ((number? arbol) arbol)
      ((symbol? arbol)
       (cond
         ((eq? arbol 'x) x)
         (else y)))
      (else (evalOps (map (lambda (subArb) (evalArbol subArb x y)) (cdr arbol)) (car arbol))))))


;==== PROCESO GENÉTICO ====;
;--- Cercania XYZ ---;
; Dominio: Un árbol de operaciones A.
; Codominio: Un número real que representa la cercanía o COTA_ERROR.
(define cercaniaXYZ
  (lambda (A)
    (cond
      ((null? A) COTA_ERROR)
      (else
       (foldl
        sumaValida
        0
        (map (lambda (p) (cond ((eq? (evalArbol A (car p) (cadr p)) 'error) COTA_ERROR)
                 (else (expValida (/ (restaValida (caddr p) (evalArbol A (car p) (cadr p))) (+ 1 (abs (caddr p)))) 2))))
             pts))))))

;--- TotalGen ---;
; Dominio: Una lista difs con los valores de cercaniaXYZ de los individuos.
; Codominio: Un número real que representa el total acumulado.
(define totalGen
  (lambda (difs) 
    (apply + (map (lambda (d) (cond
                                  ((equal? d 'error) (/ 1 1000))
                                  (else (/ 1 (+ 1 d))))) difs))))

;--- Fitness ---;
; Dominio: Una lista difs de valores de cercanía y un número real total.
; Codominio: Una lista de números reales que representan los valores de fitness.
(define fitness
  (lambda (difs total)
    (map (lambda (d) (cond
                      ((equal? d 'error) (/ (/ 1 1000) total))
                      (else (/ (/ 1 (+ 1 d)) total)))) difs)))

;--- Escoger élite ---;
; Dominio: Una lista de individuos S, una lista de valores de fitness F y un número real max.
; Codominio: Un individuo de la lista S.
(define escogerElite
  (lambda (S F max)
    (cond
      ((or (null? S) (null? F)) S)
      ((equal? (car F) max) (car S))
      (else (escogerElite (cdr S) (cdr F) max)))))


;==== AUXILIARES MUTACIÓN ====;
;--- Elegir num rand ---;
; Dominio: Un árbol A.
; Codominio: Un número real (hoja del árbol) o 0 si no hay hojas numéricas.
(define elegirNumRand
  (lambda (A)
    (cond
      ((null? (filter number? (nivel A (altura A)))) 0)
      (else (elementoEn (filter number? (nivel A (altura A))) 
          (random (length (filter number? (nivel A (altura A)))))))
      )))
   
;--- Elegir hoja ---;
; Dominio: Un árbol A y una ruta.
; Codominio: Una lista que representa la ruta hacia una hoja.
(define elegirHoja
  (lambda (A ruta)
    (cond
      ((or (null? A) (not (list? A))) (cons A ruta))
      ((< (random) 0.5 ) (cons 0 (elegirHoja (cadr A) ruta)))
      (else (cons 1 (elegirHoja (caddr A) ruta))))))

;--- Cambiar elemento ---;
; Dominio: Un árbol A, una ruta R y un elemento e.
; Codominio: El árbol A con el elemento en la ruta R reemplazado por e.
(define cambiarElemento
  (lambda (A R e)
  (cond
    ((null? R) e)
    ((= 0 (car R)) (list (car A) (cambiarElemento (cadr A) (cdr R) e ) (caddr A)))
    (else (list (car A) (cadr A) (cambiarElemento (caddr A) (cdr R) e))))))

;--- Swap de hojas ---;
; Dominio: Un árbol A y dos rutas R1 y R2.
; Codominio: El árbol A con las hojas en R1 y R2 intercambiadas.
(define swapHojas
  (lambda (A R1 R2)
    (cambiarElemento (cambiarElemento A (pop R2) (top R1)) (pop R1) (top R2))))

;--- Cambiar operación ---;
; Dominio: Un árbol A, un entero nvl y un operador op.
; Codominio: El árbol A con el operador cambiado en el nivel dado.
(define cambiarOp
  (lambda (A nvl op)
    (cond
      ((or (not (list? A)) (null? A)) A)
      ((zero? nvl) (list op (cadr A) (caddr A)))
      ((and (not (list? (cadr A))) (not (list? (caddr A))) 
            (not (member (cadr A) opsPosibles equal?)) (not (member (caddr A) opsPosibles equal?)))
        (list op (cadr A) (caddr A)))
      ((<= (random) 0.5) (list (car A) (cambiarOp (cadr A) (- nvl 1) op) (caddr A)))
      (else (list (car A) (cadr A) (cambiarOp (caddr A) (- nvl 1) op))))))

;--- Cambiar número ---;
; Dominio: Un árbol A, un entero nvl, un número nuevoNum y una hoja.
; Codominio: El árbol A con la hoja reemplazada por nuevoNum en el nivel dado.
(define cambiarNum
  (lambda (A nvl nuevoNum hoja)
    (cond 
      ((or (not (list? A)) (null? A)) A)
      ((zero? nvl) (cond ((equal? A hoja) nuevoNum) 
                          (else A)))
      ((list? A) (list (car A) (cambiarNum (cadr A) (- nvl 1) nuevoNum hoja) 
                                (cambiarNum (caddr A) (- nvl 1) nuevoNum hoja)))
      (else A))))

;--- Cambiar números ---;
; Dominio: Un árbol A, un entero nvl y un número real num.
; Codominio: Un árbol con números modificados aleatoriamente en un nivel.
(define cambiarNums
  (lambda (A nvl num)
    (cond 
      ((or (not (list? A)) (null? A)) 
       (cond ((number? A) (precisionK (+ A (* (- (* 2 (random)) 1) num A)) 6))
             (else A)))
      ((zero? nvl) A)
      ((list? A) (list (car A) (cambiarNums (cadr A) (- nvl 1) num) 
                                (cambiarNums (caddr A) (- nvl 1) num)))
      (else A))))

;--- Cambiar estructura ---;
; Dominio: Un árbol A.
; Codominio: Un árbol con su estructura cambiada aleatoriamente.
(define cambiarEstruc
  (lambda (A)
    (cond
      ((or (null? A) (not (list? A)) (< (altura A) 2)) A)
      ((< (random) 0.3) (list (elementoEn opsPosibles (random (length opsPosibles))) 
                              (caddr A) (cadr A)))
      ((< (random) 0.6) (list (car A) 
                              (list (elementoEn opsPosibles (random (length opsPosibles)))
                                    (cadr A) (hoja-aleatoria (random)))
                              (caddr A)))
      (else (list (car A) (cadr A)
                  (list (elementoEn opsPosibles (random (length opsPosibles)))
                        (caddr A) (hoja-aleatoria (random))))))))

;--- Quitar rama aux ---;
; Dominio: Un árbol A y una ruta.
; Codominio: El árbol A con la subrama eliminada en la ruta indicada.
(define quitar-rama-aux
  (lambda (A ruta)
    (cond
      ((or (not (list? A)) (null? A)) A)
      ((null? ruta) (arbol-altura-n 1))
      ((= 0 (car ruta)) (list (car A) (quitar-rama-aux (cadr A) (cdr ruta)) (caddr A)))
      (else (list (car A) (cadr A) (quitar-rama-aux (caddr A) (cdr ruta)))))))

;--- Quitar rama ---;
; Dominio: Un árbol A y una ruta.
; Codominio: El árbol A con una rama eliminada en una posición aleatoria de la ruta.
(define quitar-rama
  (lambda (A ruta)
    (quitar-rama-aux A (primeros-n ruta (+ 1 (random (- (length ruta) 2)))))))

;--- Pegar subárbol ---;
; Dominio: Un árbol A y una ruta.
; Codominio: El árbol A con un subárbol aleatorio pegado en la ruta indicada.
(define pegar-subArbol
  (lambda (A ruta)
    (cond 
      ((null? ruta) (arbol-aleatorio (+ 1 (random 1)) (+ 1 (random 1))))
      ((zero? (car ruta)) (list (car A) (pegar-subArbol (cadr A) (cdr ruta)) (caddr A)))
      (else (list (car A) (cadr A) (pegar-subArbol (caddr A) (cdr ruta)))))))

;--- Cambiar hojas variables ---;
; Dominio: Un árbol A.
; Codominio: El árbol A con las hojas reemplazadas aleatoriamente por 'x o 'y.
(define cambiar-hojas-variables
  (lambda (A)
    (cond
      ((or (not (list? A)) (null? A)) (cond ((< (random) 0.5) 'x) (else 'y)))
      (else (list (car A) (cambiar-hojas-variables (cadr A)) (cambiar-hojas-variables (caddr A)))))))


;==== MUTACIONES Y CRUCES ====;
;--- Supermutar ---;
; Dominio: Un árbol A y un número real p.
; Codominio: Un árbol A con modificaciones aleatorias.
(define supermutar
  (lambda (A p)
    (cond
      ((< p 0.5) (quitar-rama A (elegirHoja A '())))
      ((< p 0.75) (cambiar-hojas-variables A)) 
      (else (pegar-subArbol A (pop (elegirHoja A '())))))))

;--- Mutar ---;
; Dominio: Un árbol A y un número real p.
; Codominio: Un árbol A mutado aleatoriamente.
(define mutar
  (lambda (A p)
    (cond
      ((< (altura A) 1) A)
      ((< p 0.15) (cambiarOp A (random (altura A)) (elementoEn opsPosibles (random (length opsPosibles))) ))
      ((< p 0.30) (cambiarNum A (altura A) (realRand) (elegirNumRand A)))
      ((< p 0.45) (cambiarNums A (altura A) 0.1))
      ((< p 0.60) (swapHojas A (elegirHoja A '()) (elegirHoja A '())))
      ((< p 0.75) (cambiarEstruc A))
      ((< p 0.90) (cambiarOp (cambiarNum A (altura A) (realRand) (elegirNumRand A)) 
                             (random (altura A)) 
                             (elementoEn opsPosibles (random (length opsPosibles)))))
      (else (cambiarNums (cambiarEstruc A) (altura A) 0.05)))))

;--- Cruce con altura balanceada ---;
; Dominio: Dos árboles A y B.
; Codominio: Un árbol resultado del cruce de A y B.
(define cruce-balanceado
  (lambda (A B)
    (cond
      ((or (null? A) (null? B)) (cond ((null? A) B) (else A)))
      ((and (< (abs (- (altura A) (altura B))) 2) (> (random) 0.5))
       (list (car A) (cadr B) (caddr A)))
      (else (list (car B) (cadr A) (caddr B))))))

;--- Mezclar árboles aux ---;
; Dominio: Dos árboles A y B.
; Codominio: Un árbol resultado del cruce de A y B.
(define mezclar-arboles-aux
  (lambda (A B)
    (cond
      ((or (null? A) (null? B) (not (list? A)) (not (list? B))) 
       (cond ((< (random) 0.5) A) (else B)))
      (else (list (cond ((< (random) 0.5) (car A)) (else (car B)))
                  (mezclar-arboles-aux (cadr A) (cadr B))
                  (mezclar-arboles-aux (caddr A) (caddr B)))) )))

;--- Mezclar árboles ---;
; Dominio: Dos árboles A y B.
; Codominio: Un árbol resultado del cruce de A y B.
(define mezclar-arboles
  (lambda (A B)
    (cond
      ((or (null? A) (null? B)) (cond ((null? A) B) (else A)))
      (else (mezclar-arboles-aux A B)) )))

;--- Cruces aux ---;
; Dominio: Un árbol cruce y un valor-cruce numérico.
; Codominio: Un árbol cruce posiblemente mutado o supermutado.
(define cruces-aux
  (lambda (cruce valor-cruce)
    (cond
      ((< (random) 0.05) (supermutar cruce (random)))
      ((< (random) 0.1) (mutar cruce (random)))
      (else cruce) )))

;--- Cruces ---;
; Dominio: Dos árboles A y B.
; Codominio: Un nuevo árbol resultado del cruce de A y B.
(define cruces
  (lambda(A B)
  (cond
    ((null? B) A)
    ((null? A) B)
    ((< (random) 0.4) (cruces-aux (cruce-balanceado A B) (cercaniaXYZ (cruce-balanceado A B))))
    ((< (random) 0.7) (cruces-aux (mezclar-arboles A B) (cercaniaXYZ (mezclar-arboles A B))))
    (else (cruces-aux (list (car A) (cadr A) (caddr B)) 
      (cercaniaXYZ (list (car A) (cadr A) (caddr B))) )))))

;--- Seleccionar Intervalo ---;
; Dominio: Una lista de valores de fitness y un número real rango.
; Codominio: Una lista de números reales que representan intervalos acumulados.
(define selectIntervalo
  (lambda (listaFits rango)
    (cond
      ((null? listaFits) (list))
      (else (cons (+ (car listaFits) rango) (selectIntervalo (cdr listaFits) (+ rango (car listaFits))))))))

;--- Seleccionar Individuo ---;
; Dominio: Una lista genActual, una lista de intervalos listaInterv y un número real rand.
; Codominio: Un árbol seleccionado de genActual.
(define selectIndiv
  (lambda (genActual listaInterv rand)
   (cond
    ((null? genActual) genActual)
    ((< rand (car listaInterv)) (car genActual))
    (else (selectIndiv (cdr genActual) (cdr listaInterv) rand)))))



;==== GENERACIÓN DE ÁRBOLES ====;
;--- Hoja aleatoria ---;
; Dominio: Un número real p.
; Codominio: Un número real positivo o el símbolo 'x o 'y.
(define hoja-aleatoria
  (lambda (p)
    (cond
        ((< p 0.33334) (realPositiveRand) )
        ((< p 0.66667) 'x)
        (else 'y))))

;--- Arbol altura n ---;
; Dominio: Un número entero n (altura del árbol).
; Codominio: Un árbol de altura n con hojas aleatorias.
(define arbol-altura-n 
  (lambda (n)
    (cond
      ((zero? n) (hoja-aleatoria (realRand)))
      (else (list (elementoEn opsPosibles (random (length opsPosibles))) (arbol-altura-n (- n 1)) (arbol-altura-n (- n 1)))))))

;--- Arbol aleatorio ---;
; Dominio: Dos números enteros n (cantidad de subárboles) y m (altura máxima).
; Codominio: Un árbol aleatorio con n subárboles de altura ≤ m.
(define arbol-aleatorio
  (lambda (n m)
    (cond
      ((= n 1) (arbol-altura-n (+ 2 (random 3))))
      (else (list (elementoEn opsPosibles (random (length opsPosibles))) (arbol-aleatorio (- n 1) m) (arbol-aleatorio (- n 1) m)))))) 


;==== MANEJO DE POBLACIONES ====;
;--- Generar poblacion ---;
; Dominio: Un número entero n.
; Codominio: Una lista de árboles aleatorios de tamaño n.
(define generarPoblacion
  (lambda (n)
    (cond
      ((zero? n) (crearSet))
      (else (cons (arbol-aleatorio (+ 1 (random 2)) (+ 1 (random 2))) (generarPoblacion (- n 1)))))))

;--- Mejor individuo Gen ---;
; Dominio: Una lista genActual de árboles y una lista de valores de fitness fits.
; Codominio: Una lista con el árbol más apto (elite) de genActual.
(define mejorGen
  (lambda (genActual fits)
    ;(print "Cerc") (print (map (lambda (ind) (cercaniaXYZ ind)) genActual)) (newline) (newline)
    ;(print "ELITE: ") (print (escogerElite genActual fits (apply max fits))) (newline) (newline)
    ;(print (fitness (map (lambda (ind) (cercaniaXYZ ind)) genActual) (totalGen (map (lambda (ind) (cercaniaXYZ ind)) genActual)))) (newline) (newline)
    (list (escogerElite genActual fits (apply max fits)) )))

;--- Generar descendencia aux---;
; Dominio: Una lista de árboles descendencia, lista de árboles padres, número entero n, lista de números reales entre 0 y 1 inclusivo fits y un árbol cruce.
; Codominio: Una lista de árboles.
(define generar-descendencia-aux
  (lambda(DES P n fits cruce)
  (cond
  ((zero? n) DES)
  ((member cruce DES equal?) (generar-descendencia-aux DES P n fits (cruces (selectIndiv P (selectIntervalo fits 0) (random)) (selectIndiv P (selectIntervalo fits 0) (random)))))
  (else (cons cruce (generar-descendencia-aux DES P (- n 1) fits (cruces (selectIndiv P (selectIntervalo fits 0) (random)) (selectIndiv P (selectIntervalo fits 0) (random))) ))))))

;--- Generar descendencia ---;
; Dominio: Una lista de árboles descendencia, lista de árboles padres, número entero n, lista de números reales entre 0 y 1 inclusivo fits.
; Codominio: Una lista de árboles.
(define generar-descendencia
  (lambda (DES P n fits)
    (generar-descendencia-aux DES P n fits 
      (cruces (selectIndiv P (selectIntervalo fits 0) (random)) (selectIndiv P (selectIntervalo fits 0) (random))))))

;--- Obtener fitness ---;
; Dominio: Lista de árboles de alguna generación.
; Codominio: Lista de números reales entre 0 y 1 inclusivo.
(define obt-fitness
  (lambda (PG)
      (fitness (map (lambda (ind) (cercaniaXYZ ind)) PG)
          (totalGen (map (lambda (ind) (cercaniaXYZ ind)) PG)))))

;--- Algoritmo Genético ---;
; Dominio: Lista de árboles de una generación, número entero n.
; Codominio: Un árbol.
(define algoritmo-genetico
  (lambda(PG n)
  (cond
  ;((zero? n) (print "fin-gen") (newline) (car (mejorGen PG (obt-fitness PG))))
  ((>= 0.05 (cercaniaXYZ (car (mejorGen PG (obt-fitness PG) )))) (car (mejorGen PG (obt-fitness PG))))
  (else (algoritmo-genetico (generar-descendencia (mejorGen PG (obt-fitness PG) ) PG (- (length PG) 1) (obt-fitness PG)) (- n 1))))))


;==== DEFINICIÓN DE PUNTOS ====;
(define pts1
  '((19 15 29346.35851851852)
    (2 9 1.0534979423868314)
    (2 2 21.333333333333332)
    (6 14 105.79591836734694)
    (7 10 448.18666666666667)
    (7 18 138.32921810699588)
    (6 5 829.44)
    (2 13 0.504930966469428)
    (2 4 5.333333333333333)
    (6 14 105.79591836734694)))

(define pts2
  '((4 17 -2.4497720911755554)
    (18 18 89295665425899.97)
    (14 15 -5886448.888255767)
    (5 1 0.15592448293623584)
    (6 0 0.09047539154589039)
    (2 11 -0.011618783380305042)
    (3 12 -0.12606209562358486)
    (15 16 -38931442.58443023)
    (9 12 -1318.6359677907499)
    (12 14 -181011.55860384842)
    (4 15 -1.5999968706280614)
    (7 3 0.7676317512752314)
    (13 4 14.10577688348565)
    (7 4 1.7429185241417005)
    (9 10 -972.9858294278807)
    (17 3 25.555036074522636)
    (2 8 -0.022675969371921127)
    (7 18 -1355.8618354315115)
    (6 17 -150.36511151795014)
    (2 3 -0.29251189747407047)
    (17 19 -2711501213.613263)
    (19 12 2381741.3955791583)
    (9 0 0.1219744326565336)
    (7 0 0.09461557702483518)
    (2 2 2.718281828459045)
    (8 14 -847.1487092209862)
    (5 14 -8.697010469045177)
    (9 2 0.5807556858098182)
    (2 13 -0.008237769270189607)
    (11 2 1.0412422649541293)
    (6 10 -12.716588105740703)
    (17 12 751887.6135417097)
    (8 0 0.10499644237143123)
    (12 18 -1882257.8027239882)
    (2 2 2.718281828459045)
    (17 6 701.4163925167654)
    (3 14 -0.1365009957082285)
    (12 5 24.875518175753292)
    (2 11 -0.011618783380305042)
    (19 5 581.8154927034864)))

(define pts3
  '((7 19 0.021467462815455557)
    (15 5 0.8972897520305314)
    (3 12 0.034300505648069725)
    (4 16 0.02618158100704072)
    (5 3 0.3234657858799432)
    (14 10 0.09287287780685931)
    (9 17 0.02363015892994423)
    (17 17 0.06919428556089095)
    (17 1 2.88274645317702e+86)
    (19 3 110.46198121467432)
    (15 17 0.03143716981725172)
    (19 8 0.0535003236864474)
    (3 16 0.02618158100704072)
    (10 8 0.0502589029357979)
    (2 18 0.02219768929739122)
    (19 15 0.11946085767729647)
    (4 12 0.034300505648069725)
    (4 8 0.060623809023872356)
    (5 5 0.4031782747928774)
    (17 17 0.03294052780160192)
    (16 14 0.0877734809884482)
    (5 4 0.21784251308036023)
    (14 3 4.655292450035863)
    (12 2 17.95581981811268)
    (12 2 4978.651181453867)
    (15 18 0.02586177411353612)
    (10 2 1.473903445060754)
    (17 8 3.109408219964699)
    (9 16 0.033032040793022216)
    (12 9 0.059983006928122015)
    (16 17 0.023835458764299447)
    (17 13 0.030687867723187132)
    (8 6 0.13131681414605237)
    (6 1 0.657744623479457)
    (14 13 0.08003365790226269)
    (7 14 0.030372418985094643)
    (9 2 91.18717719247881)
    (9 11 0.04724676957863885)
    (8 15 0.0281155302113515)
    (14 7 56.43618334181874)))

(define pts4
  '((7 1 8)
    (18 13 31)
    (9 9 18)
    (14 4 18)
    (10 9 19)
    (18 1 19)
    (16 5 21)
    (0 16 16)
    (19 11 30)
    (10 9 19)
    (14 4 18)
    (12 18 30)
    (3 11 14)
    (10 0 10)
    (17 17 34)
    (2 0 2)
    (17 15 32)
    (15 1 16)
    (2 18 20)
    (4 1 5)
    (13 19 32)
    (5 19 24)
    (14 8 22)
    (9 18 27)
    (16 12 28)
    (3 12 15)
    (15 12 27)
    (0 15 15)
    (13 18 31)
    (9 19 28)
    (7 4 11)
    (19 19 38)
    (14 19 33)
    (6 6 12)
    (8 17 25)
    (9 3 12)
    (14 2 16)
    (3 1 4)
    (13 5 18)
    (12 6 18)))

(define pts
  '((6 10 -64)
    (6 9 -45)
    (19 9 280)
    (18 19 -37)
    (6 5 11)
    (7 18 -275)
    (2 4 -12)
    (7 16 -207)
    (1 5 -24)
    (4 18 -308)
    (0 13 -169)
    (1 8 -63)
    (11 14 -75)
    (7 17 -240)
    (0 19 -361)
    (2 0 4)
    (15 9 144)
    (13 0 169)
    (12 14 -52)
    (10 11 -21)
    (3 6 -27)
    (1 11 -120)
    (10 7 51)
    (0 9 -81)
    (14 19 -165)
    (9 15 -144)
    (4 10 -84)
    (3 8 -55)
    (9 1 80)
    (16 19 -105)
    (2 4 -12)
    (17 11 168)
    (14 19 -165)
    (19 11 240)
    (7 2 45)
    (5 14 -171)
    (6 14 -160)
    (4 2 12)
    (1 7 -48)
    (2 3 -5)))

(define pts6
  '((11 3 0.6274735630753033)
    (11 2 0.5404763088546395)
    (17 11 0.8711164930263436)
    (19 18 0.983974444213354)
    (10 17 1.184929413512433)
    (3 6 1.2920296742201791)
    (8 13 1.1760912590556811)
    (7 13 1.2324867603589635)
    (13 15 1.0462189153299533)
    (3 11 1.5936926411670824)
    (6 7 1.0566416671474377)
    (4 5 1.0860331325016919)
    (1 2 1.2618595071429148)
    (11 19 1.1869717539906937)
    (16 19 1.0533324751067528)
    (18 16 0.9648297958439285)
    (4 9 1.3382908331057726)
    (19 12 0.8668214419824227)
    (4 18 1.6719500161730103)
    (14 5 0.701838730514401)
    (3 12 1.6397385131955606)
    (4 5 1.0860331325016919)
    (5 10 1.276989408269624)
    (0 17 4.247927513443585)
    (7 13 1.2324867603589635)
    (12 9 0.9086181061280522)
    (13 5 0.7185650207899779)
    (19 12 0.8668214419824227)
    (10 5 0.783091851446946)
    (4 15 1.5812464746045694)
    (2 0 0.5)
    (11 19 1.1869717539906937)
    (6 12 1.2691183073525347)
    (1 5 1.7712437491614221)
    (7 13 1.2324867603589635)
    (4 7 1.226294385530917)
    (8 17 1.2787536009528289)
    (14 1 0.3962406251802891)
    (14 9 0.8648579046593244)
    (3 17 1.8294828004351504)))


;==== INTERFAZ PLOT 3D ====
(plot-new-window? #f)
(define plot-frame #f)
(define plot-canvas #t)
(define last-renderer3d #t)
(define ANGULO 65)
(define ALTITUD 20)

(define convertir-arbol
  (lambda (arbol)
    (lambda (x y) (evalArbol arbol x y))))

(define pts-vectores
  (lambda (pts)
    (map (lambda (p) (vector (car p) (cadr p) (caddr p))) pts)))

(define rangos-xy
  (lambda (pts)
    (values (apply min (map car pts))
            (apply max (map car pts))
            (apply min (map cadr pts))
            (apply max (map cadr pts)))))

(define plot-canvas%
  (class canvas%
    (super-new)
    (define/override (on-paint)
      (when last-renderer3d
    (define dc (send this get-dc))
    (define w (send this get-width))
    (define h (send this get-height))
    (plot3d/dc last-renderer3d dc 0 0 w h #:angle ANGULO #:altitude ALTITUD)))))

(define ensure-plot-window
  (lambda ()
    (when (not plot-frame)
      (set! plot-frame (new frame% [label "Elites 3D"] [width 700] [height 700]))
      (set! plot-canvas (new plot-canvas% [parent plot-frame]))
      (send plot-frame show #t))))

(define graficar-puntos
  (lambda (pts)
    (ensure-plot-window)
    (set! last-renderer3d (list (points3d (pts-vectores pts) #:sym 'fullcircle #:size 6 #:color "red")))
    (send plot-canvas refresh)))

(define graficar-func-arbol
  (lambda (arbol pts)
    (ensure-plot-window)
    (call-with-values
      (lambda () (rangos-xy pts))
      (lambda (xmin xmax ymin ymax)
        (set! last-renderer3d
              (list (surface3d (convertir-arbol arbol) xmin xmax ymin ymax #:color "Powder Blue" #:line-color "Violet")
                    (points3d (pts-vectores pts) #:sym 'fullcircle #:size 6 #:color "red")))
        (send plot-canvas refresh)))))

(define mostrar-res
  (lambda (arbol pts)
    (parameterize ([plot-new-window? #t]
               [plot-width  (send plot-frame get-width)]
               [plot-height (send plot-frame get-height)])
      (call-with-values
        (lambda () (rangos-xy pts))
        (lambda (xmin xmax ymin ymax)
          (plot3d (list (surface3d (convertir-arbol arbol) xmin xmax ymin ymax #:color "Powder Blue" #:line-color "Violet")
                        (points3d (pts-vectores pts) #:sym 'fullcircle #:size 6 #:color "red"))
                  #:angle ANGULO
                  #:altitude ALTITUD))))))

(define algoritmo-genetico-ui
  (lambda (PG restantes n k actualizar-plot finalizar)
    (define fits (obt-fitness PG))
    (define elite (escogerElite PG fits (apply max fits)))
    (define gene (- n restantes))
    (when (or (zero? gene) (zero? (remainder gene k))) (actualizar-plot elite gene))
    (cond
      ((zero? restantes) (finalizar elite))
      ((>= 0.05 (cercaniaXYZ elite)) (finalizar elite))
      (else (algoritmo-genetico-ui
             (generar-descendencia (list elite) PG (- (length PG) 1) fits)
             (- restantes 1) n k actualizar-plot finalizar)))))

(define interfaz-plot
  (lambda (PG n k pts)
    (define resultado (box #f))
    (define actualizar-plot
      (lambda (elite gene)
        (queue-callback
         (lambda ()
           (graficar-func-arbol elite pts)
           (set! ANGULO (+ ANGULO 15))
           (send plot-canvas refresh)
           (print "Cercania: ") (print (cercaniaXYZ elite)) (newline) (print "Elite: ") (print elite) (newline) (newline)) #t)))
    (define finalizar
      (lambda (elite)
        (set-box! resultado elite)
        (queue-callback (lambda () (mostrar-res elite pts)) #t)))
    (thread (lambda () (algoritmo-genetico-ui PG n n k actualizar-plot finalizar)))
    resultado))

(graficar-puntos pts)
(define gen (generarPoblacion 100))
(define res (interfaz-plot gen 10000 10 pts))
(unbox res)