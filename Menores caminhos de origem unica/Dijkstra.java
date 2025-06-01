import java.util.*;

// Classe que representa o grafo
class GrafoDijkstra {

    // Vetor que guarda os vértices do grafo
    private final Vertice[] vertices;
    // Lista de adjacência: cada índice contém a lista de arestas do vértice correspondente
    private List<List<Aresta>> adjacencias;

    // Construtor do grafo que recebe os nomes dos vértices
    GrafoDijkstra(String[] nomesVertices) { // Exemplo: {S, T, Y, X, Z}
        // Inicializa a lista principal de adjacências
        this.adjacencias = new ArrayList<>();
        // Cria o vetor de vértices
        this.vertices = new Vertice[nomesVertices.length];

        for (int i = 0; i < nomesVertices.length; i++) {
            // Inicializa a lista de vizinhos para cada vértice
            this.adjacencias.add(new ArrayList<>());
            // Cria cada vértice com nome e índice
            this.vertices[i] = new Vertice(nomesVertices[i], i);
        }
    }

    // Método para adicionar uma aresta direcionada ao grafo
    public void adicionarAresta(int origem, int destino, int peso) {
        // Adiciona uma nova aresta à lista de vizinhos do vértice de origem
        adjacencias.get(origem).add(new Aresta(destino, peso));
    }

    // Retorna a lista de arestas/vizinhos de um vértice dado pelo índice
    public List<Aresta> vizinhos(int u) {
        return adjacencias.get(u);
    }

    // Retorna o vetor de vértices do grafo
    public Vertice[] getVertices() {
        return vertices;
    }

    // Retorna a quantidade de vértices do grafo
    public int quantidadeVertices() {
        return vertices.length;
    }

    // Classe interna que representa um vértice do grafo
    static class Vertice {
        String nome; // Nome do vértice (ex: "s", "t", etc.)
        int indice; // Índice do vértice no vetor
        int distancia; // Distância estimada a partir da origem (inicialmente infinita)
        Vertice predecessor; // Predecessor no caminho mínimo

        // Construtor de vértice
        Vertice(String nome, int indice) {
            this.nome = nome;
            this.indice = indice;
            this.distancia = Integer.MAX_VALUE; // Inicia com "infinito"
            this.predecessor = null; // Sem predecessor inicialmente
        }

        @Override
        public String toString() { // Facilita a depuração e visualização
            return nome + "(d=" + (distancia == Integer.MAX_VALUE ? "inf" : distancia) + ")";
        }
    }

    // Classe interna que representa uma aresta do grafo
    static class Aresta {
        int destino; // Índice do vértice de destino
        int peso;   // Peso da aresta

        // Construtor de aresta
        Aresta(int destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }
}

// Classe que implementa o algoritmo de Dijkstra
public class Dijkstra {

    // Método que executa o algoritmo de Dijkstra a partir do vértice de origem 's'
    public void executar(GrafoDijkstra g, int s) {
        // Inicializa as distâncias de todos os vértices como infinito e predecessores como nulos.
        // A distância do vértice de origem 's' é definida como 0.
        inicializarFonteUnica(g, s);

        // Cria uma lista 'q' que simula uma fila de prioridade.
        // Contém todos os vértices do grafo.
        // Em uma implementação otimizada, isso seria uma java.util.PriorityQueue.
        List<GrafoDijkstra.Vertice> q = new ArrayList<>(List.of(g.getVertices()));

        // Loop principal do algoritmo: continua enquanto houver vértices na lista 'q' (fila de prioridade).
        while (!q.isEmpty()) {
            // Extrai o vértice 'u' de 'q' com a menor estimativa de distância 'u.distancia'.
            // Este vértice 'u' é considerado "finalizado", seu caminho mais curto foi encontrado.
            var u = extrairMinimo(q);

            // Para cada vértice 'v' adjacente a 'u' (ou seja, para cada aresta (u,v))...
            for (GrafoDijkstra.Aresta aresta : g.vizinhos(u.indice)) {
                // 'aresta.destino' é o índice do vértice 'v'.
                // 'aresta.peso' é o peso da aresta (u,v).
                // A operação 'relaxar' tenta diminuir a estimativa de distância de 'v'
                // considerando o caminho através de 'u'.
                relaxar(u, g.getVertices()[aresta.destino], aresta.peso);
            }
        }
    }

    // Operação de Relaxamento:
    // Se o caminho atual para 'v' é mais longo do que o caminho para 'u' somado ao peso da aresta (u,v),
    // então encontramos um caminho mais curto para 'v' passando por 'u'.
    // Atualizamos a distância de 'v' e definimos 'u' como seu predecessor.
    private void relaxar(GrafoDijkstra.Vertice u, GrafoDijkstra.Vertice v, int pesoArestaUV) {
        // Verifica se a distância de 'v' pode ser melhorada passando por 'u'.
        // u.distancia != Integer.MAX_VALUE é uma verificação para evitar overflow se u é inalcançável.
        if (u.distancia != Integer.MAX_VALUE && u.distancia + pesoArestaUV < v.distancia) {
            v.distancia = u.distancia + pesoArestaUV; // Atualiza a nova menor distância para 'v'.
            v.predecessor = u; // 'u' se torna o predecessor de 'v' no caminho mais curto.
        }
    }

    // Inicializa os atributos 'distancia' e 'predecessor' de todos os vértices.
    // Define a distância da origem 's' como 0 e dos demais como infinito (Integer.MAX_VALUE).
    // Define todos os predecessores como nulos.
    private void inicializarFonteUnica(GrafoDijkstra g, int s) {
        for (GrafoDijkstra.Vertice v : g.getVertices()) {
            v.distancia = Integer.MAX_VALUE;
            v.predecessor = null;
        }
        g.getVertices()[s].distancia = 0; // A distância da origem para ela mesma é 0.
    }

    // Simula a extração do elemento com prioridade mínima (menor distância) de uma fila de prioridade.
    // Percorre a lista 'q' para encontrar o vértice com a menor 'distancia' e o remove da lista.
    // Complexidade: O(N) onde N é o tamanho de 'q'.
    // Usar java.util.PriorityQueue (heap) reduziria esta operação para O(log N).
    private GrafoDijkstra.Vertice extrairMinimo(List<GrafoDijkstra.Vertice> q) {
        if (q.isEmpty()) return null;

        int indiceMin = 0;
        // Encontra o índice do vértice com a menor distância.
        for (int i = 1; i < q.size(); i++) {
            if (q.get(i).distancia < q.get(indiceMin).distancia) {
                indiceMin = i;
            }
        }
        // Remove e retorna o vértice encontrado.
        return q.remove(indiceMin);
    }

    // Método principal de execução/teste do algoritmo
    public static void main(String[] args) {
        // Cria vetor com os nomes dos vértices do grafo
        String[] nomes = {"s", "t", "x", "y", "z"}; // s=0, t=1, x=2, y=3, z=4

        // Instancia um grafo com os nomes informados
        GrafoDijkstra g = new GrafoDijkstra(nomes);

        // Adiciona arestas ao grafo com seus respectivos pesos
        g.adicionarAresta(0, 1, 10); // s -> t (peso 10)
        g.adicionarAresta(0, 3, 5);  // s -> y (peso 5)
        g.adicionarAresta(1, 2, 1);  // t -> x (peso 1)
        g.adicionarAresta(1, 3, 2);  // t -> y (peso 2)
        g.adicionarAresta(2, 4, 4);  // x -> z (peso 4)
        g.adicionarAresta(3, 1, 3);  // y -> t (peso 3)
        g.adicionarAresta(3, 2, 9);  // y -> x (peso 9)
        g.adicionarAresta(3, 4, 2);  // y -> z (peso 2)
        g.adicionarAresta(4, 0, 7);  // z -> s (peso 7) 
        g.adicionarAresta(4, 2, 6);  // z -> x (peso 6)

        // Cria uma instância do algoritmo de Dijkstra e executa a partir do vértice 's' (índice 0)
        Dijkstra dijkstra = new Dijkstra();
        dijkstra.executar(g, 0); // Origem 's' (índice 0)

        // Exibe os resultados: distância mínima e predecessor de cada vértice
        System.out.println("Resultados do Algoritmo de Dijkstra (origem: s):");
        for (GrafoDijkstra.Vertice v : g.getVertices()) {
            System.out.printf("Vértice %s: Distância = %s, Predecessor = %s\n",
                    v.nome,
                    (v.distancia == Integer.MAX_VALUE ? "inf" : v.distancia),
                    (v.predecessor != null ? v.predecessor.nome : "Nenhum")
            );
        }
    }

/*

O Algoritmo de Dijkstra é um algoritmo guloso que resolve o problema do caminho mais curto de uma única origem
para grafos ponderados direcionados ou não direcionados, **desde que todos os pesos das arestas sejam não negativos**.

Princípios Fundamentais:
1.  **Conjunto de Vértices Finalizados (S):** O algoritmo mantém um conjunto S de vértices para os quais o
    caminho mais curto a partir da origem 's' já foi determinado. Inicialmente, S é vazio.
2.  **Estimativas de Distância (d[v]):** Para cada vértice v, d[v] é uma estimativa do peso do caminho mais
    curto de 's' a 'v'. $d[s]$ é inicializado com 0 e $d[v]$ com infinito para $v \neq s$.
3.  **Predecessores (π[v]):** $\pi[v]$ armazena o predecessor de 'v' no caminho mais curto conhecido.
4.  **Seleção Gulosa:** Em cada passo, o algoritmo seleciona o vértice $u \notin S$ com a menor estimativa $d[u]$.
    Este vértice 'u' é adicionado a S. Esta é a escolha gulosa. A não negatividade dos pesos garante que,
    uma vez que 'u' é adicionado a S, $d[u]$ é de fato a distância do caminho mais curto para 'u'.
5.  **Relaxamento de Arestas:** Após adicionar 'u' a S, todas as arestas $(u,v)$ que saem de 'u' são "relaxadas".
    A operação de relaxamento para uma aresta $(u,v)$ com peso $w(u,v)$ verifica se o caminho para 'v'
    passando por 'u' é mais curto do que o valor atual de $d[v]$. Se $d[u] + w(u,v) < d[v]$, então $d[v]$
    é atualizado para $d[u] + w(u,v)$ e $\pi[v]$ é definido como 'u'.

Estrutura de Dados Principal:
-   **Fila de Prioridade Mínima (Min-Priority Queue):** Essencial para a eficiência. Armazena os vértices que
    não estão em S (isto é, V-S), usando $d[v]$ como chave de prioridade.
    Operações:
    - `INSERT(v, Q)`: Insere o vértice v na fila Q.
    - `EXTRACT-MIN(Q)`: Remove e retorna o vértice u de Q com a menor chave $d[u]$.
    - `DECREASE-KEY(v, nova_distancia, Q)`: Atualiza a chave do vértice v na fila Q para um novo valor
      menor (ocorre durante o relaxamento se $d[v]$ diminui).

    O código fornecido utiliza uma `java.util.List` e um método `extrairMinimo` que busca linearmente
    o mínimo. Isso leva a uma complexidade de $O(V)$ para `extrairMinimo`.
    - Complexidade com Lista: $O(V^2 + E) = O(V^2)$ para grafos densos.
    - Complexidade com Heap Binário: $O((V+E)\log V) = O(E \log V)$ para grafos esparsos.
    - Complexidade com Heap de Fibonacci: $O(V \log V + E)$.

Passos do Algoritmo:
1.  `INITIALIZE-SINGLE-SOURCE(G, s)`:
    Para cada vértice $v \in G.V$:
        $v.distancia = \infty$
        $v.predecessor = \text{null}$
    $s.distancia = 0$
2.  $S = \emptyset$ (conjunto de vértices finalizados)
3.  $Q = G.V$ (fila de prioridade com todos os vértices, usando `distancia` como chave)
4.  Enquanto $Q \neq \emptyset$:
    a. $u = \text{EXTRACT-MIN}(Q)$  (extrai o vértice com menor $d[u]$)
    b. $S = S \cup \{u\}$
    c. Para cada vértice $v$ adjacente a $u$ (ou seja, para cada aresta $(u,v) \in G.E$):
        i. $\text{RELAX}(u, v, w(u,v))$
           Se $v.distancia > u.distancia + w(u,v)$:
               $v.distancia = u.distancia + w(u,v)$
               $v.predecessor = u$
               (Se usando fila de prioridade que suporta `DECREASE-KEY`, chamar aqui)

Exemplo de Teste de Mesa (baseado no `main` e Figura 24.2 Cormen, 3ª ed.):
Vértices: s(0), t(1), x(2), y(3), z(4)
Origem: s (índice 0)

Inicialização:
d = [0, inf, inf, inf, inf]
π = [null, null, null, null, null]
Q = [s(0), t(inf), x(inf), y(inf), z(inf)] (ordenado por 'd')

Iteração 1:
- u = extrairMinimo(Q) -> s (d=0). Q = [t(inf), x(inf), y(inf), z(inf)]. S = {s}
- Vizinhos de s:
  - Aresta (s,t,10): relaxar(s,t,10) -> d[t] = 0+10=10, π[t]=s. Q atualizado (conceitualmente): [y(inf), x(inf), z(inf), t(10)]
  - Aresta (s,y,5):  relaxar(s,y,5)  -> d[y] = 0+5=5,  π[y]=s. Q atualizado: [y(5), t(10), x(inf), z(inf)]
  (Ordem em Q após relaxamentos: [y(5), t(10), x(inf), z(inf), desconsiderando s que foi extraído])

Iteração 2:
- u = extrairMinimo(Q) -> y (d=5). Q = [t(10), x(inf), z(inf)]. S = {s,y}
- Vizinhos de y:
  - Aresta (y,t,3): relaxar(y,t,3) -> d[t] > d[y]+3? (10 > 5+3=8). Sim. d[t]=8, π[t]=y. Q: [t(8), x(inf), z(inf)]
  - Aresta (y,x,9): relaxar(y,x,9) -> d[x] > d[y]+9? (inf > 5+9=14). Sim. d[x]=14, π[x]=y. Q: [t(8), x(14), z(inf)]
  - Aresta (y,z,2): relaxar(y,z,2) -> d[z] > d[y]+2? (inf > 5+2=7). Sim. d[z]=7, π[z]=y. Q: [z(7), t(8), x(14)]

Iteração 3:
- u = extrairMinimo(Q) -> z (d=7). Q = [t(8), x(14)]. S = {s,y,z}
- Vizinhos de z:
  - Aresta (z,s,7): relaxar(z,s,7) -> d[s] > d[z]+7? (0 > 7+7=14). Não. (s já está em S, sua distância é final)
  - Aresta (z,x,6): relaxar(z,x,6) -> d[x] > d[z]+6? (14 > 7+6=13). Sim. d[x]=13, π[x]=z. Q: [t(8), x(13)]

Iteração 4:
- u = extrairMinimo(Q) -> t (d=8). Q = [x(13)]. S = {s,y,z,t}
- Vizinhos de t:
  - Aresta (t,x,1): relaxar(t,x,1) -> d[x] > d[t]+1? (13 > 8+1=9). Sim. d[x]=9, π[x]=t. Q: [x(9)]
  - Aresta (t,y,2): relaxar(t,y,2) -> d[y] > d[t]+2? (5 > 8+2=10). Não. (y já está em S)

Iteração 5:
- u = extrairMinimo(Q) -> x (d=9). Q = []. S = {s,y,z,t,x}
- Vizinhos de x:
  - Aresta (x,z,4): relaxar(x,z,4) -> d[z] > d[x]+4? (7 > 9+4=13). Não. (z já está em S)

Q está vazia. Fim.

Resultados Finais:
s: d=0, π=Nenhum
t: d=8, π=y
x: d=9, π=t
y: d=5, π=s
z: d=7, π=y
*/
}
