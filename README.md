# IC-4700 — Lenguajes de Programación

<p align="center">
  <img alt="Racket" src="https://img.shields.io/badge/Racket-9F1D20?style=flat-square&logo=racket&logoColor=white">
  <img alt="Prolog" src="https://img.shields.io/badge/Prolog-E61B23?style=flat-square">
  <img alt="Erlang" src="https://img.shields.io/badge/Erlang-A90533?style=flat-square&logo=erlang&logoColor=white">
  <img alt="Java" src="https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white">
  <img alt="C" src="https://img.shields.io/badge/C-555555?style=flat-square&logo=c&logoColor=white">
  <a href="LICENSE"><img alt="Licencia GPL-3.0" src="https://img.shields.io/github/license/tonys183/IC-4700-Lenguajes-Proyectos?style=flat-square&label=Licencia&logo=opensourceinitiative&logoColor=white"></a>
</p>

<p align="center">
  <a href="#proyecto-i-programación-genética">Proyecto I</a> · <a href="#proyecto-ii-mastermind">Proyecto II</a> · <a href="#proyecto-iii-motor-de-póker-horse">Proyecto III</a>
</p>

Proyectos del curso Lenguajes de Programación (IC-4700), desarrollados con Racket/Scheme, Prolog, Erlang, Java y C. Los trabajos exploran distintos paradigmas y la integración de lenguajes para resolver problemas de búsqueda, generación de funciones y juegos.

## Proyecto I (Programación genética)

El proyecto consistía en generar una función de dos variables que aproximara un conjunto de puntos tridimensionales. A partir de muestras de la forma $(x, y, z)$, se buscaba una expresión capaz de producir valores cercanos a los observados, utilizando programación genética para mejorar sucesivamente una población de funciones candidatas.

Para resolverlo, se representaron las expresiones como árboles en Racket/Scheme y se implementaron mecanismos de generación aleatoria, evaluación del error, selección, elitismo, cruce y mutación. La evaluación contempla operaciones que pueden producir resultados inválidos, como divisiones entre cero o logaritmos fuera de su dominio. Además, se incorporó una visualización en tres dimensiones para comparar los puntos de entrada con la superficie de la mejor función y observar la evolución de la búsqueda.

## Proyecto II (Mastermind)

El proyecto consistía en implementar el juego Mastermind y un jugador capaz de deducir un código secreto a partir de las pistas obtenidas en cada intento. Las respuestas distinguen los colores que coinciden en su posición de aquellos que aparecen en el código, pero en una posición diferente. El trabajo buscaba combinar la programación funcional y la lógica mediante dos módulos que pudieran interactuar por consola.

La solución divide las responsabilidades entre Erlang y Prolog. El módulo en Erlang genera el código secreto, recibe los intentos y calcula las pistas; el jugador en Prolog conserva el historial de jugadas y busca combinaciones compatibles con las respuestas anteriores. Para reducir las posibilidades, utiliza restricciones y depuración de colores y posiciones, apoyándose en la generación de candidatos y el backtracking. La interacción se realiza trasladando los intentos y las pistas entre las consolas de ambos programas.

## Proyecto III (Motor de póker HORSE)

El proyecto consistía en desarrollar un motor de póker para variantes del formato HORSE, combinando la programación orientada a objetos en Java con la programación estructurada en C. La propuesta debía coordinar jugadores, reparto de cartas, turnos, apuestas y distribución del pozo, además de evaluar las manos según las reglas de la modalidad de juego.

La implementación separa la interfaz gráfica y la coordinación de las partidas en Java del análisis de manos en C, conectando los componentes mediante sockets. El código Java organiza las cartas, jugadores, mesas, comandos de juego y manejo del pozo, mientras que el componente en C se encarga de evaluar las combinaciones de cartas. También se desarrolló un cliente gráfico con JavaFX que recibe las actualizaciones de la partida y permite al usuario interactuar con la mesa.
