import java.util.*;

public class GrafoDFS { // Renomeado para evitar conflito

    // Número de vértices do grafo
    private int V;
    // Array de listas de adjacência.
    // Cada LinkedList em adj[i] armazena os vizinhos do vértice i.
    private LinkedList<Integer>[] adj;

    // Construtor do grafo
    // V é o número de vértices.
    public GrafoDFS(int V) {
        this.V = V;
        adj = new LinkedList[V]; // Inicializa o array de listas

        // Para cada vértice, cria uma nova LinkedList para armazenar seus vizinhos.
        for (int i = 0; i < V; i++) {
            adj[i] = new LinkedList<>();
        }
    }

    // Método para adicionar uma aresta (conexão) entre 'origem' e 'destino'.
    // Em um grafo não-direcionado, a conexão é mútua.
    public void adicionarAresta(int origem, int destino) {
        adj[origem].add(destino); // Adiciona 'destino' à lista de vizinhos de 'origem'.
        adj[destino].add(origem); // Adiciona 'origem' à lista de vizinhos de 'destino'.
    }

    // Método que realiza a busca em profundidade (DFS) iterativa a partir de um vértice de início.
    public void dfs(int inicio) {
        // Vetor para controlar quais vértices já foram visitados.
        // 'false' por padrão, indicando que nenhum vértice foi visitado ainda.
        boolean[] visitado = new boolean[V];
        // Pilha para gerenciar os vértices a serem explorados.
        Stack<Integer> pilha = new Stack<>();

        // Passo 1 da DFS: Empilha o vértice inicial.
        pilha.push(inicio);

        // Loop principal da DFS: continua enquanto houver vértices na pilha para explorar.
        while (!pilha.isEmpty()) {
            // Remove o vértice do topo da pilha. Este é o próximo vértice a ser explorado "profundamente".
            int atual = pilha.pop();

            // Se o vértice 'atual' ainda não foi visitado...
            // (Se já foi visitado, ele foi empilhado por outro caminho, mas já processado, então ignoramos)
            if (!visitado[atual]) {
                System.out.print(atual + " "); // Processa o vértice (neste caso, imprime).
                visitado[atual] = true;        // Marca como visitado.

                // Empilha todos os vizinhos não visitados do vértice 'atual'.
                // A ordem de iteração sobre os vizinhos (e, portanto, de empilhamento)
                // pode afetar a ordem exata da travessia DFS.
                // Por exemplo, se os vizinhos de 'atual' são [A, B, C] e são empilhados nessa ordem,
                // C será o primeiro a ser desempilhado e explorado (LIFO).
                // Para uma correspondência com a DFS recursiva padrão (que explora o primeiro vizinho da lista),
                // os vizinhos devem ser empilhados em ordem reversa à da lista de adjacência.
                // No entanto, a implementação atual (empilhar na ordem da lista) é uma forma válida de DFS iterativa.
                for (int vizinho : adj[atual]) {
                    if (!visitado[vizinho]) {
                        pilha.push(vizinho);
                    }
                }
            }
        }
        System.out.println(); // Nova linha ao final da impressão do DFS
    }

    public static void main(String[] args) {
        // Lê o número de vértices a partir do primeiro argumento da linha de comando.
        int numVertices = Integer.parseInt(args[0]);

        // Cria o grafo com o número de vértices informado.
        GrafoDFS grafo = new GrafoDFS(numVertices);

        // Lê os pares de vértices (arestas) dos argumentos restantes.
        for (int i = 1; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                int origem = Integer.parseInt(args[i]);
                int destino = Integer.parseInt(args[i + 1]);
                grafo.adicionarAresta(origem, destino);
            }
        }
        
        int noInicialDFS = 0; // No seu exemplo, DFS começa de 0.

        System.out.println("DFS a partir do vértice " + noInicialDFS + ":");
        grafo.dfs(noInicialDFS);
    }

/*
A Busca em Profundidade (DFS) é um algoritmo para percorrer ou buscar em uma estrutura de dados de grafo.
A estratégia da DFS é explorar o mais "fundo" possível ao longo de cada ramo antes de retroceder (backtracking).

Princípios Chave:
1.  **Uso de Pilha (Stack) ou Recursão:**
    - **Versão Iterativa (como no código):** Utiliza uma pilha explícita. Quando um vértice 'u' é visitado,
      seus vizinhos não visitados são empilhados. O próximo vértice a ser processado é o que está no topo
      da pilha (LIFO - Last-In, First-Out).
    - **Versão Recursiva:** A pilha de chamadas do sistema gerencia implicitamente o backtracking. Uma função
      DFS recursiva visita um nó, marca-o como visitado, e então chama a si mesma para cada vizinho não visitado.

2.  **Descoberta e Finalização (Conceitual - Tempos de Descoberta/Finalização):**
    Em implementações mais detalhadas (especialmente recursivas, como em Cormen et al.), a DFS pode registrar
    os "tempos de descoberta" ($d[u]$) e "tempos de finalização" ($f[u]$) para cada vértice 'u'.
    - $d[u]$: O "tempo" (contador global incrementado) em que 'u' é descoberto (torna-se cinza).
    - $f[u]$: O "tempo" em que a exploração a partir de 'u' termina (todos os descendentes na árvore DFS
      foram visitados, e 'u' torna-se preto).
    Esses tempos são úteis para várias aplicações, como ordenação topológica e detecção de ciclos.
    A versão iterativa fornecida foca na travessia e não calcula explicitamente esses tempos,
    mas o array `visitado` cumpre a função de evitar revisitas.

3.  **Cores (Conceitual):**
    - **Branco:** Vértice não visitado.
    - **Cinza:** Vértice descoberto, mas ainda sendo explorado (na pilha ou em uma chamada recursiva ativa).
    - **Preto:** Vértice totalmente explorado (todos os seus vizinhos alcançáveis foram visitados e ele foi
      removido da pilha/chamada recursiva retornou).
    No código iterativo: um vértice se torna `visitado[atual] = true` quando é processado (equivalente a cinza/preto).
    Um nó na pilha que ainda não teve `visitado[topo_pilha] = true` pode ser considerado cinza.

4.  **Estrutura de Árvore/Floresta DFS:** A DFS produz uma "floresta DFS", que consiste em uma ou mais "árvores DFS".
    As arestas pelas quais um novo vértice branco é descoberto são chamadas de "arestas de árvore". Outros tipos
    de arestas (de retorno, avanço, cruzamento) podem ser classificados usando os tempos de descoberta/finalização.

Passos Gerais da DFS Iterativa (aplicado ao método `dfs`):
1.  Inicialize `visitado[v] = false` para todos os vértices 'v'.
2.  Crie uma pilha S vazia.
3.  Para o vértice de início `inicio`:
    a. Empurre `inicio` na pilha S: `S.push(inicio)`.
4.  Enquanto a pilha S não estiver vazia:
    a. Retire um vértice 'u' do topo da pilha: `u = S.pop()`.
    b. Se `visitado[u]` for `false`:
        i. Marque `visitado[u] = true`.
        ii. Processe 'u' (ex: imprima).
        iii. Para cada vizinho 'v' de 'u' na lista de adjacência `adj[u]`:
            1. Se `visitado[v]` for `false`, empurre 'v' na pilha S: `S.push(v)`.
    (Nota: A ordem de empilhamento dos vizinhos afeta a ordem da travessia. Para simular a DFS
    recursiva que processa vizinhos na ordem da lista de adjacência, os vizinhos deveriam ser
    empilhados na ordem inversa da lista de adjacência. A implementação dada empilha na ordem
    em que aparecem na lista de adjacência, o que significa que o "último" vizinho na lista
    será explorado primeiro.)

Complexidade:
- Tempo: $O(V + E)$, onde V é o número de vértices e E é o número de arestas. Cada vértice é empilhado e desempilhado no máximo uma vez ($O(V)$). Cada aresta é examinada no máximo duas vezes ($O(E)$) ao percorrer as listas de adjacência.
- Espaço: $O(V)$ no pior caso para a pilha (e.g., um grafo caminho) e para o array `visitado`.

Aplicações:
- **Detecção de Ciclos:** Uma aresta de retorno (para um ancestral cinza na árvore DFS) indica um ciclo.
- **Ordenação Topológica:** Em um Grafo Acíclico Direcionado (DAG), os vértices podem ser linearmente ordenados de
  forma que para toda aresta direcionada (u,v), 'u' venha antes de 'v'. A DFS pode ser usada emitindo os vértices
  na ordem inversa de seus tempos de finalização.
- **Componentes Fortemente Conectados:** Em grafos direcionados.
- **Pathfinding:** Encontrar um caminho entre dois nós (não necessariamente o mais curto).


TESTE DE MESA - ENTRADA :
java GrafoDFS 6 0 1 0 2 1 3 2 4 4 5
(DFS iniciando em 0)

Lista de adjacência (exemplo, a ordem dos vizinhos pode variar):
adj[0]: [1, 2] (se 1 foi adicionado antes de 2)
adj[1]: [0, 3]
adj[2]: [0, 4]
adj[3]: [1]
adj[4]: [2, 5]
adj[5]: [4]

Simulação do dfs(0):
1.  `visitado` = [F,F,F,F,F,F], `pilha` = []
2.  `pilha.push(0)` => `pilha` = [0]

3.  Loop 1:
    `atual = pilha.pop()` (0) => `pilha` = []
    `!visitado[0]` (true):
        Print 0. `visitado[0]=T`.
        Vizinhos de 0: 1, 2.
        `!visitado[1]` => `pilha.push(1)`. `pilha` = [1]
        `!visitado[2]` => `pilha.push(2)`. `pilha` = [1, 2] (2 no topo)

4.  Loop 2:
    `atual = pilha.pop()` (2) => `pilha` = [1]
    `!visitado[2]` (true):
        Print 2. `visitado[2]=T`.
        Vizinhos de 2: 0, 4.
        `visitado[0]` é true.
        `!visitado[4]` => `pilha.push(4)`. `pilha` = [1, 4] (4 no topo)

5.  Loop 3:
    `atual = pilha.pop()` (4) => `pilha` = [1]
    `!visitado[4]` (true):
        Print 4. `visitado[4]=T`.
        Vizinhos de 4: 2, 5.
        `visitado[2]` é true.
        `!visitado[5]` => `pilha.push(5)`. `pilha` = [1, 5] (5 no topo)

6.  Loop 4:
    `atual = pilha.pop()` (5) => `pilha` = [1]
    `!visitado[5]` (true):
        Print 5. `visitado[5]=T`.
        Vizinhos de 5: 4.
        `visitado[4]` é true.

7.  Loop 5:
    `atual = pilha.pop()` (1) => `pilha` = []
    `!visitado[1]` (true):
        Print 1. `visitado[1]=T`.
        Vizinhos de 1: 0, 3.
        `visitado[0]` é true.
        `!visitado[3]` => `pilha.push(3)`. `pilha` = [3] (3 no topo)

8.  Loop 6:
    `atual = pilha.pop()` (3) => `pilha` = []
    `!visitado[3]` (true):
        Print 3. `visitado[3]=T`.
        Vizinhos de 3: 1.
        `visitado[1]` é true.

9.  `pilha` está vazia. Fim.

Saída: 0 2 4 5 1 3
Esta ordem corresponde à sua simulação, assumindo que `adj[0]` é iterado como `[1, 2]`, fazendo com que `2` seja empilhado por último e, portanto, processado primeiro entre os vizinhos de `0`.
*/
}