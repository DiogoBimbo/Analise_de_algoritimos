import java.util.*;

public class GrafoBFS {

    // Número total de vértices do grafo
    private int vertices;
    // Lista de adjacência: cada vértice aponta para uma lista de vizinhos
    // Usamos um array de LinkedLists, onde o índice do array representa o vértice
    // e a LinkedList nesse índice contém os vizinhos desse vértice.
    private List<Integer>[] adjacencias;

    // Construtor que inicializa o grafo com um número específico de vértices
    // e a lista de adjacência.
    public GrafoBFS(int vertices) {
        this.vertices = vertices;
        this.adjacencias = new LinkedList[vertices]; // Cria o array de listas

        // Para cada vértice, inicializa sua lista de adjacência como uma LinkedList vazia.
        // Isso é necessário porque o array é apenas de "contêineres" de lista,
        // e cada lista precisa ser instanciada.
        for (int i = 0; i < vertices; i++) {
            adjacencias[i] = new LinkedList<>();
        }
    }

    // Método para adicionar uma aresta (conexão) entre dois vértices.
    // Como o grafo é não direcionado (uma conexão de v1 para v2 implica
    // uma conexão de v2 para v1), adicionamos a aresta em ambas as listas
    // de adjacência.
    public void adicionarArestas(int v1, int v2) {
        this.adjacencias[v1].add(v2); // Adiciona v2 à lista de vizinhos de v1
        this.adjacencias[v2].add(v1); // Adiciona v1 à lista de vizinhos de v2
    }

    // Método que encontra a menor distância (número de arestas) entre dois vértices usando BFS
    public void menorCaminho(int origem, int destino) {
        // Array para armazenar a distância de cada vértice a partir da origem.
        // Inicialmente, as distâncias são desconhecidas (ou poderiam ser inicializadas com infinito).
        int[] distancias = new int[vertices];
        // Array para marcar os vértices já visitados (ou que já entraram na fila).
        // Isso evita que um vértice seja processado múltiplas vezes.
        boolean[] visitados = new boolean[vertices];

        // Fila usada para implementar a BFS. Armazena os vértices a serem visitados.
        Queue<Integer> fila = new LinkedList<>();

        // Passo 1 da BFS: Inicializar a origem.
        fila.offer(origem);             // Enfileira o vértice de origem.
        distancias[origem] = 0;         // A distância da origem para ela mesma é 0.
        visitados[origem] = true;       // Marca a origem como visitada.

        // Loop principal da BFS: continua enquanto houver vértices na fila para explorar.
        while (!fila.isEmpty()) {
            // Remove o próximo vértice da fila para processá-lo.
            // Este é o vértice 'u' na teoria, o mais antigo na fila (mais próximo da origem).
            int atual = fila.poll();

            // Para cada vizinho 'v' do vértice 'atual'...
            for (int vizinho : adjacencias[atual]) {
                // Se o vizinho ainda não foi visitado...
                if (!visitados[vizinho]) {
                    fila.offer(vizinho);                            // Enfileira o vizinho para exploração futura.
                    visitados[vizinho] = true;                      // Marca o vizinho como visitado.
                    distancias[vizinho] = distancias[atual] + 1;    // A distância do vizinho é a distância do 'atual' + 1.

                    // Otimização: Se encontramos o destino, já que a BFS garante o menor caminho
                    // em grafos não ponderados, podemos parar e imprimir o resultado.
                    if (vizinho == destino) {
                        System.out.printf("Distância mínima de %d até %d é %d\n", origem, destino, distancias[vizinho]);
                        return; // Encerra o método, pois o menor caminho foi encontrado.
                    }
                }
            }
        }

        // Se o loop terminar e o destino não foi encontrado (a condição 'if (vizinho == destino)'
        // nunca foi verdadeira e retornou), significa que não há caminho da origem ao destino.
        System.out.printf("Não há caminho de %d até %d\n", origem, destino);
    }

    public static void main(String[] args) {
        // O primeiro argumento da linha de comando indica a quantidade de vértices.
        int numVertices = Integer.parseInt(args[0]);
        GrafoBFS grafo = new GrafoBFS(numVertices); // Cria o grafo com o número de vértices.

        // Os próximos argumentos (até os dois últimos) indicam as arestas, em pares.
        // Ex: arg[1] e arg[2] formam uma aresta, arg[3] e arg[4] formam outra, etc.
        for (int i = 1; i < args.length - 2; i += 2) {
            int v1 = Integer.parseInt(args[i]);
            int v2 = Integer.parseInt(args[i + 1]);
            grafo.adicionarArestas(v1, v2); // Adiciona a aresta ao grafo.
        }

        // Os dois últimos argumentos representam o vértice de origem e o vértice de destino.
        int origem = Integer.parseInt(args[args.length - 2]);
        int destino = Integer.parseInt(args[args.length - 1]);

        // Calcula e exibe a menor distância entre a origem e o destino usando BFS.
        grafo.menorCaminho(origem, destino);
    }

/*

A Busca em Largura (BFS) é um algoritmo de travessia de grafos que explora os vértices "camada por camada".
Partindo de um vértice fonte 's', a BFS primeiro visita todos os vértices que estão a uma distância de 1 aresta de 's',
depois todos os vértices a uma distância de 2 arestas, e assim por diante.

Princípios Chave:
1.  **Uso de Fila (Queue):** A BFS utiliza uma estrutura de dados de fila (FIFO - First-In, First-Out) para
    gerenciar os vértices a serem visitados. Quando um vértice é descoberto, ele é colocado no final da fila.
    O próximo vértice a ser explorado é sempre aquele que está no início da fila (o que foi descoberto há mais tempo
    entre os que ainda não foram totalmente explorados).

2.  **Descoberta e Processamento:**
    - Um vértice é "descoberto" na primeira vez que é encontrado durante a travessia. Neste momento, ele é
      colocado na fila e marcado como visitado (para evitar ciclos e redundância).
    - Um vértice é "processado" ou "explorado" quando é removido da fila. Neste momento, seus vizinhos
      são examinados.

3.  **Cores (Conceitual):** Frequentemente, na teoria de grafos (como em Cormen et al.), os vértices são
    categorizados por cores para rastrear o estado da BFS:
    - **Branco:** Vértice ainda não descoberto.
    - **Cinza:** Vértice descoberto, mas ainda não totalmente explorado (seus vizinhos ainda não foram todos
      examinados). Vértices na fila são cinzas.
    - **Preto:** Vértice totalmente explorado (todos os seus vizinhos foram descobertos).
    No código fornecido, o array `visitados` (boolean) cumpre uma função similar: `false` pode ser visto
    como Branco, e `true` como Cinza/Preto (especificamente, um vértice se torna `true` ao ser enfileirado,
    ou seja, ao se tornar Cinza).

4.  **Cálculo de Distâncias:** A BFS é ideal para encontrar o caminho mais curto (em termos de número de arestas)
    em grafos não ponderados. A distância $d[s,v]$ do vértice fonte 's' para qualquer vértice 'v' é o menor número
    de arestas em um caminho de 's' a 'v'.
    - A distância da fonte para ela mesma é 0 ($d[s,s] = 0$).
    - Para qualquer vizinho 'v' de um vértice 'u' que é descoberto a partir de 'u', a distância é $d[s,v] = d[s,u] + 1$.
    O array `distancias` no código implementa essa lógica.

5.  **Árvore de Busca em Largura:** A BFS constrói implicitamente uma "árvore de busca em largura". Se o vértice 'v'
    foi descoberto a partir do vértice 'u', então a aresta (u, v) faz parte dessa árvore, e 'u' é o predecessor
    (ou pai) de 'v'. Embora o código não armazene explicitamente os predecessores, eles são parte do processo.

Passos Gerais da BFS (aplicado ao método `menorCaminho`):
1.  Inicialize todos os vértices:
    - `distancias[v] = ∞` (ou um valor indicando não alcançado) para todo $v \neq s$.
    - `distancias[s] = 0`.
    - `visitados[v] = false` para todo $v$.
    (No código, `distancias` é inicializado com 0 por padrão em Java para `int[]`, mas `distancias[origem]` é explicitamente 0).
2.  Crie uma fila Q vazia.
3.  Enfileire o vértice de origem 's': `Q.offer(s)`. Marque `visitados[s] = true`.
4.  Enquanto a fila Q não estiver vazia:
    a. Desenfileire um vértice 'u': `u = Q.poll()`.
    b. Para cada vizinho 'v' de 'u' na lista de adjacência `adjacencias[u]`:
        i. Se `visitados[v]` for `false` (ou seja, 'v' é branco):
            1. Marque `visitados[v] = true` (v torna-se cinza).
            2. Atualize a distância: `distancias[v] = distancias[u] + 1`.
            3. (Opcional: armazene `predecessor[v] = u`).
            4. Enfileire 'v': `Q.offer(v)`.
            5. **Otimização para `menorCaminho`**: Se `v` for o `destino`, a distância `distancias[v]` é a menor.
               Pode-se parar aqui.
    (Após 'u' ser desenfileirado e todos os seus vizinhos processados, 'u' pode ser considerado "preto").

Complexidade:
- Tempo: $O(V + E)$, onde V é o número de vértices e E é o número de arestas. Cada vértice é enfileirado e desenfileirado no máximo uma vez ($O(V)$). Cada aresta é examinada no máximo duas vezes (uma para cada ponta em grafos não direcionados) ao explorar os vizinhos ($O(E)$).
- Espaço: $O(V)$ para armazenar a fila, o array de visitados e o array de distâncias.


TESTE DE MESA - ENTRADA :
java GrafoBFS 6 0 1 0 3 1 5 2 5 3 4 4 5 0 5

Número de vértices: 6 (indexados de 0 a 5)
Arestas: 0-1, 0-3, 1-5, 2-5, 3-4, 4-5
Origem: 0, Destino: 5

Execução do menorCaminho(0, 5):
1. Inicialização:
   fila = [], distancias = [0,...0] (default), visitados = [F,...,F]

2. Origem 0:
   fila.offer(0)         => fila = [0]
   distancias[0] = 0
   visitados[0] = true     => visitados = [T, F, F, F, F, F]

3. Loop (atual = 0): fila = []
   Vizinhos de 0: 1, 3
   - Para vizinho 1: !visitados[1]
     fila.offer(1)         => fila = [1]
     visitados[1] = true     => visitados = [T, T, F, F, F, F]
     distancias[1] = distancias[0] + 1 = 1
   - Para vizinho 3: !visitados[3]
     fila.offer(3)         => fila = [1, 3]
     visitados[3] = true     => visitados = [T, T, F, T, F, F]
     distancias[3] = distancias[0] + 1 = 1

4. Loop (atual = 1): fila = [3]
   Vizinhos de 1: 0, 5
   - Para vizinho 0: visitados[0] é true, ignora.
   - Para vizinho 5: !visitados[5]
     fila.offer(5)         => fila = [3, 5]
     visitados[5] = true     => visitados = [T, T, F, T, F, T]
     distancias[5] = distancias[1] + 1 = 2
     vizinho (5) == destino (5) -> Encontrado!
     Imprime: "Distância mínima de 0 até 5 é 2"
     return;

Saída Esperada:
Distância mínima de 0 até 5 é 2
*/
}