#lang scheme

;==== Funciones auxiliares ====;

;--- Altura ---;
; Dominio:
; Codominio:
(define altura
  (lambda (A)
    (cond
      ((null? A) -1)
      ((not (list? A)) 0)
      (else (+ 1 (max (altura (cadr A)) (altura (caddr A))))))))

;--- Nivel ---;
; Dominio:
; Codominio:
(define nivel
  (lambda (A n)
    (cond
      ((null? A) (list))
      ((zero? n) (list car A))
      ((list? A) (append (nivel (cadr A) (- n 1)) (nivel (caddr A) (- n 1))))
      (else (list)))))

;--- Elemento en Índice ---;
; Dominio:
; Codominio:
(define elementoEn
  (lambda (L n)
    (cond
      ((zero? n) (car L))
      (else (elementoEn (cdr L) (- n 1))))))


;--- Válidación de Operaciones ---;

(define divValida
  (lambda (a b)
    (cond
      ((= b 0) 'error)
      (else (/ a b)))))


(define expValida
  (lambda (b e)
    (cond
      ((and (zero? b) (< e 0)) 'error)
      ((and (< b 0) (not (integer? e)))
       (cond
         ((complex? (- (expt (- b) e))) 'error) ;Complex? no sirve del todo
            (else (- (expt (- b) e)))))
      (else (expt b e)))))

(define logValido
  (lambda (a b)
    (cond
      ((or (< a 0) (<= b 1)) 'error)
      (else (/ (log a) (log b))))))




  
;--- Definición de operadores ---;
(define opsValidas (list
                    (cons '+ +)
                    (cons '- -)
                    (cons '* *)
                    (cons '/ divValida)
                    (cons 'loga_b logValido)
                    (cons '** expValida)))




;--- Puntos y Operaciones Posibles ---

(define pts '((0 1 4) (1 3 5) (2 5 6)))
(define opsPosibles '('+ '- '* '/ 'loga_b '**))


;--- SET ---

(define crearSet
  (lambda () (list)))

(define insertarSet
  (lambda (S x)
    (cond
      ((member x S equal?) S)
      (else (cons x S)))))





;--- Manejo de Individuos ---

(define evalOps
  (lambda (paramOp op)
    (cond
      ((member 'error paramOp equal?) 'error)
      (else (apply op paramOp)))))


(define evalArbol
  (lambda (arbol x y)
    (cond
      ((number? arbol) arbol)
      ((symbol? arbol)
       (cond
         ((eq? arbol 'x) x)
         ((eq? arbol 'y) y)
         (else arbol)))
      (else (evalOps (map (lambda (subArb) (evalArbol subArb x y)) (cdr arbol)) (cdr (assoc (car arbol) opsValidas)))))))





;--- Proceso Genético ---;

(define cercaniaXYZ 
  (lambda (A)
    (cond
      ((member 'error (map (lambda (p)
                            (cond
                             ((eq? 'error (evalArbol A (car p) (cadr p))) 'error)
                             (else (expt (- (caddr p) (evalArbol A (car p) (cadr p))) 2)))) pts)) 'error)
      (else (apply + (map (lambda (p)
                            (cond
                             ((eq? 'error (evalArbol A (car p) (cadr p))) 'error)
                             (else (expt (- (caddr p) (evalArbol A (car p) (cadr p))) 2)))) pts))))))


(define totalGen
  (lambda (difs) ;Lista de cercaniaXYZ de todos los individuos
    (apply + (map (lambda (d) (cond
                                  ((equal? d 'error) (/ 1 1000))
                                  (else (/ 1 (+ 1 d))))) difs))))

(define fitness
  (lambda (difs total)
    (map (lambda (d) (cond
                      ((equal? d 'error) (/ (/ 1 1000) total))
                      (else (/ (/ 1 (+ 1 d)) total)))) difs)))


(define escogerElite
  (lambda (S F max)
    (cond
      ((= (car F) max) (car S))
      (else (escogerElite (cdr S) (cdr F) max)))))




;==== Auxiliares para Mutación ====;

;--- Cambiar Operador ---;
(define cambiarOp
  (lambda (A nvl op)
    (cond
      ((null? A) A)
      ((zero? nvl) (list op (cadr A) (caddr A)))
      ((and (not (list? (cadr A))) (not (list? (caddr A))) 
            (not (member (cadr A) opsPosibles equal?)) (not (member (caddr A) opsPosibles equal?)))
        (list op (cadr A) (caddr A)))
      ((<= (random) 0.5) (list (car A) (cambiarOp (cadr A) (- nvl 1) op) (caddr A)))
      (else (list (car A) (cadr A) (cambiarOp (caddr A) (- nvl 1) op)))
    )))


;--- Cambiar Número ---;
(define cambiarNum
  (lambda (A nvl nuevoNum hoja)
  (cond 
      ((null? A) A)
      ((zero? nvl) (cond ((equal? A hoja) nuevoNum) 
                         (else A)))
      ((list? A) (list (car A) (cambiarNum (cadr A) (- nvl 1) nuevoNum hoja) 
                               (cambiarNum (caddr A) (- nvl 1) nuevoNum hoja)))
      (else A))))




;==== Probar Funciones ====;
(define arbol-completo '(+ (* (/ 10 y) (loga_b 8 2)) (** x 2)))

;--- Datos ---;
(define indiv '(A B C D E F G))
(define fits (fitness '(error 11.34 21.38 12.123 56.93 12.21 11.56) 0.3755023920535431))

(define nivelHojas (altura arbol-completo))
(define hojas (filter number? (nivel arbol-completo nivelHojas)))
(define hojaRand (elementoEn hojas (random (length hojas))))


;--- Funciones ---;
;(cambiarNum arbol-completo nivelHojas 5 hojaRand)
;(cambiarOp arbol-completo 1 '+)


;(apply + (fitness '(error 11.34 21.38 12.123 56.93 12.21 11.56) 0.3755023920535431))
;(escogerElite indiv fits (apply max fits))




;==== Por Implementar ====;

;--- Supermutar ---;
(define supermutar
  (lambda (A p)
    (cond
    )))

;--- Mutar ---;
(define mutar
  (lambda (A p)
    (cond
      ;((< p 0.25) (tratar-hojas (filter (lambda(x) (number? x)) (nivel A (altura A))) A))
    )))

;--- Cruce ---;
(define cruce
  (lambda (A B)
    (cond
      ;((< (random) 0.05) (mutar (list (car A) (cdr A) (cddr B)) (random))) ;mutación
      ;((< (random) 0.01) (supermutar
      ;((else (list (car A) (cdr A) (cddr B))))
    )))