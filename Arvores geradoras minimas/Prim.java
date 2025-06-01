import java.util.*;

// Classe para representar uma aresta ponderada na lista de adjacência para Prim
class ArestaPrim {
    int destino; // Vértice de destino da aresta
    int peso;    // Peso da aresta

    public ArestaPrim(int destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }
}

// Classe para representar um vértice no contexto do algoritmo de Prim
// Usada principalmente para a fila de prioridade e para armazenar informações do algoritmo
class VerticePrim implements Comparable<VerticePrim> {
    int id;         // Identificador/índice do vértice
    int chave;      // Chave (key): peso da aresta mais leve que conecta este vértice a um vértice na AGM em construção
    int pai;        // Pai/predecessor na AGM
    boolean naAGM;  // Indica se o vértice já foi incluído na AGM

    public VerticePrim(int id) {
        this.id = id;
        this.chave = Integer.MAX_VALUE; // Inicialmente, a chave é infinita
        this.pai = -1;                  // Sem pai inicialmente (-1 indica nenhum)
        this.naAGM = false;             // Inicialmente, não está na AGM
    }

    @Override
    public int compareTo(VerticePrim outro) {
        return Integer.compare(this.chave, outro.chave); // Comparação baseada na chave para a fila de prioridade
    }
}

// Classe Grafo para o algoritmo de Prim
class GrafoPrim {
    private int numVertices;
    private List<List<ArestaPrim>> adjacencias; // Lista de adjacência

    public GrafoPrim(int numVertices) {
        this.numVertices = numVertices;
        adjacencias = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) {
            adjacencias.add(new ArrayList<>());
        }
    }

    // Adiciona uma aresta não direcionada (adiciona em ambas as listas de adjacência)
    public void adicionarAresta(int u, int v, int peso) {
        if (u < 0 || u >= numVertices || v < 0 || v >= numVertices) {
            System.err.println("Erro: Vértice inválido ao adicionar aresta (" + u + "," + v + ")");
            return;
        }
        adjacencias.get(u).add(new ArestaPrim(v, peso));
        adjacencias.get(v).add(new ArestaPrim(u, peso)); // Grafo não direcionado
    }

    public List<ArestaPrim> getVizinhos(int u) {
        if (u < 0 || u >= numVertices) {
            System.err.println("Erro: Vértice inválido ao obter vizinhos: " + u);
            return Collections.emptyList();
        }
        return adjacencias.get(u);
    }

    public int getNumVertices() {
        return numVertices;
    }
}

public class Prim {

    public List<String> primAGM(GrafoPrim grafo, int verticeInicial) {
        int numVertices = grafo.getNumVertices();
        if (verticeInicial < 0 || verticeInicial >= numVertices) {
            System.err.println("Vértice inicial inválido para Prim.");
            return Collections.emptyList();
        }

        VerticePrim[] verticesInfo = new VerticePrim[numVertices];
        for (int i = 0; i < numVertices; i++) {
            verticesInfo[i] = new VerticePrim(i);
        }

        // Define a chave do vértice inicial como 0 para que ele seja o primeiro a ser escolhido
        verticesInfo[verticeInicial].chave = 0;

        // Fila de prioridade para armazenar os vértices que ainda não estão na AGM,
        // ordenados por sua 'chave'.
        PriorityQueue<VerticePrim> filaPrioridade = new PriorityQueue<>();
        for (int i = 0; i < numVertices; i++) {
            filaPrioridade.add(verticesInfo[i]);
        }

        List<String> arestasAGM = new ArrayList<>();
        int pesoTotalAGM = 0;

        // O loop principal continua até que a fila de prioridade esteja vazia
        // (ou seja, todos os vértices foram adicionados à AGM).
        while (!filaPrioridade.isEmpty()) {
            // Extrai o vértice 'u' com a menor 'chave' da fila de prioridade.
            // Este vértice 'u' é adicionado à AGM.
            VerticePrim uInfo = filaPrioridade.poll();
            uInfo.naAGM = true; // Marca 'u' como estando na AGM

            // Se uInfo.pai != -1, significa que esta não é a primeira iteração (para o vértice inicial)
            // e a aresta (uInfo.pai, uInfo.id) faz parte da AGM.
            if (uInfo.pai != -1) {
                arestasAGM.add("(" + uInfo.pai + " -- " + uInfo.id + ", peso: " + uInfo.chave + ")");
                pesoTotalAGM += uInfo.chave;
            }

            // Para cada vizinho 'v' de 'u':
            for (ArestaPrim aresta : grafo.getVizinhos(uInfo.id)) {
                VerticePrim vInfo = verticesInfo[aresta.destino];

                // Se 'v' ainda não está na AGM e o peso da aresta (u,v) é menor
                // que a 'chave' atual de 'v':
                if (!vInfo.naAGM && aresta.peso < vInfo.chave) {
                    // Atualiza a 'chave' de 'v' e define 'u' como seu pai.
                    // É crucial remover e readicionar à fila de prioridade para que ela reordene
                    // com base na nova chave.
                    filaPrioridade.remove(vInfo); // Remove o estado antigo de vInfo
                    vInfo.chave = aresta.peso;
                    vInfo.pai = uInfo.id;
                    filaPrioridade.add(vInfo);    // Adiciona o estado atualizado de vInfo
                }
            }
        }
        System.out.println("Peso total da AGM (Prim): " + pesoTotalAGM);
        return arestasAGM;
    }

    public static void main(String[] args) {
        int numVertices = 4; // Vértices 0, 1, 2, 3
        GrafoPrim grafo = new GrafoPrim(numVertices);

        // Usando o mesmo grafo do exemplo de Kruskal
        grafo.adicionarAresta(0, 1, 10);
        grafo.adicionarAresta(0, 2, 6);
        grafo.adicionarAresta(0, 3, 5);
        grafo.adicionarAresta(1, 3, 15);
        grafo.adicionarAresta(2, 3, 4);

        Prim algoritmoPrim = new Prim();
        int verticeInicial = 0; // Começando pelo vértice 0
        List<String> resultadoAGM = algoritmoPrim.primAGM(grafo, verticeInicial);

        System.out.println("\nArestas da Árvore Geradora Mínima (Prim a partir de " + verticeInicial + "):");
        for (String arestaStr : resultadoAGM) {
            System.out.println(arestaStr);
        }

        // Exemplo do Cormen (Figura 23.1)
        System.out.println("\n--- Exemplo Cormen Fig 23.1 ---");
        numVertices = 9; // a=0, b=1, c=2, d=3, e=4, f=5, g=6, h=7, i=8
        GrafoPrim grafoCormen = new GrafoPrim(numVertices);
        grafoCormen.adicionarAresta(0, 1, 4); // a-b
        grafoCormen.adicionarAresta(0, 7, 8); // a-h
        grafoCormen.adicionarAresta(1, 2, 8); // b-c
        grafoCormen.adicionarAresta(1, 7, 11); // b-h
        grafoCormen.adicionarAresta(2, 3, 7); // c-d
        grafoCormen.adicionarAresta(2, 5, 4); // c-f
        grafoCormen.adicionarAresta(2, 8, 2); // c-i
        grafoCormen.adicionarAresta(3, 4, 9); // d-e
        grafoCormen.adicionarAresta(3, 5, 14); // d-f
        grafoCormen.adicionarAresta(4, 5, 10); // e-f
        grafoCormen.adicionarAresta(5, 6, 2); // f-g
        grafoCormen.adicionarAresta(6, 7, 1); // g-h
        grafoCormen.adicionarAresta(6, 8, 6); // g-i
        grafoCormen.adicionarAresta(7, 8, 7); // h-i
        
        resultadoAGM = algoritmoPrim.primAGM(grafoCormen, 0); // Começando por 'a' (índice 0)
        System.out.println("\nArestas da Árvore Geradora Mínima (Prim - Cormen, a partir de a):");
        for (String arestaStr : resultadoAGM) {
            System.out.println(arestaStr);
        }
    }
}

/*
O Algoritmo de Prim é um método guloso para encontrar uma Árvore Geradora Mínima (AGM)
em um grafo conectado, não direcionado e ponderado. Ele funciona construindo a AGM
de forma incremental, começando com um único vértice e expandindo-a uma aresta por vez.

Princípios Fundamentais:
1.  **Crescimento da Árvore:** O algoritmo mantém um conjunto de vértices que já fazem
    parte da AGM em construção (vamos chamar este conjunto de $V_{AGM}$). Inicialmente,
    $V_{AGM}$ contém apenas um vértice de partida arbitrário.

2.  **Propriedade de Corte Segura:** A cada passo, Prim adiciona uma aresta "segura" à AGM.
    Uma aresta segura é uma aresta de peso mínimo que conecta um vértice em $V_{AGM}$ a um
    vértice fora de $V_{AGM}$ (ou seja, em $V - V_{AGM}$). Adicionar tal aresta não cria
    ciclos e é garantido (pela propriedade de corte das AGMs) que ela pertence a alguma AGM.

3.  **Chaves e Predecessores:** Para cada vértice $v \notin V_{AGM}$, o algoritmo mantém:
    - `chave[v]`: O peso da aresta mais leve que conecta $v$ a algum vértice em $V_{AGM}$.
      Se não existe tal aresta (ou $v$ é o vértice inicial), `chave[v]` é $\infty$ (ou 0 para o inicial).
    - `pai[v]`: O vértice em $V_{AGM}$ que está conectado a $v$ através da aresta de peso `chave[v]`.

4.  **Fila de Prioridade Mínima:** Para selecionar eficientemente o próximo vértice a ser
    adicionado à AGM, Prim utiliza uma fila de prioridade mínima. Esta fila armazena
    todos os vértices $v \notin V_{AGM}$, usando `chave[v]` como a prioridade.

Passos do Algoritmo (usando uma fila de prioridade Q):
1.  Para cada vértice $u \in G.V$:
    a. $u.chave = \infty$
    b. $u.pai = \text{null}$
    c. $u.naAGM = \text{false}$ (ou, equivalentemente, todos os vértices estão inicialmente na fila de prioridade)
2.  Escolha um vértice de início arbitrário $r \in G.V$.
    a. $r.chave = 0$
3.  Inicialize uma fila de prioridade mínima $Q$ com todos os vértices de $G.V$, usando `chave` como chave de ordenação.
4.  Enquanto $Q$ não estiver vazia:
    a. Extraia de $Q$ o vértice $u$ com a menor `chave` (operação `EXTRACT-MIN(Q)`).
    b. Marque $u.naAGM = \text{true}$ (ou remova $u$ da consideração de "fora da AGM").
    c. (Se $u.pai \neq \text{null}$, a aresta $(u.pai, u)$ com peso $u.chave$ é parte da AGM).
    d. Para cada vizinho $v$ de $u$ (ou seja, para cada aresta $(u,v) \in G.E$):
        i. Se $v.naAGM$ é `false` (ou seja, $v \in Q$) E $peso(u,v) < v.chave$:
            1. $v.pai = u$
            2. $v.chave = peso(u,v)$
            3. Atualize a posição de $v$ na fila de prioridade $Q$ com sua nova `chave`
               (operação `DECREASE-KEY(Q, v, v.chave)`).

Corretude:
A corretude de Prim, assim como a de Kruskal, baseia-se na propriedade genérica de crescimento de AGMs.
Em cada passo, Prim adiciona a aresta de menor peso que cruza o corte entre os vértices já na
árvore ($V_{AGM}$) e os vértices fora dela ($V - V_{AGM}$). Essa aresta é garantidamente parte de alguma AGM.

Complexidade:
A complexidade do algoritmo de Prim depende da implementação da fila de prioridade:
-   **Usando um array simples para a fila de prioridade (e buscando o mínimo linearmente):**
    - `EXTRACT-MIN` leva $O(V)$.
    - `DECREASE-KEY` (implícito na atualização da chave e na próxima busca) é $O(1)$ para atualizar, mas a busca é $O(V)$.
    - O loop principal executa $V$ vezes. Dentro dele, os vizinhos são percorridos (total $O(E)$ ao longo de todas as iterações).
    - Complexidade total: $O(V^2 + E) = O(V^2)$ para grafos densos.

-   **Usando um heap binário para a fila de prioridade:**
    - Construção inicial da fila: $O(V)$.
    - `EXTRACT-MIN`: $O(\log V)$, executado $V$ vezes -> $O(V \log V)$.
    - `DECREASE-KEY`: $O(\log V)$. No pior caso, pode ser chamado $O(E)$ vezes -> $O(E \log V)$.
    - Complexidade total: $O(V \log V + E \log V) = O(E \log V)$ (assumindo grafo conectado, $E \ge V-1$).

-   **Usando um heap de Fibonacci para a fila de prioridade:**
    - `EXTRACT-MIN`: $O(\log V)$ (amortizado).
    - `DECREASE-KEY`: $O(1)$ (amortizado).
    - Complexidade total: $O(V \log V + E)$. Esta é a melhor complexidade assintótica teórica para Prim.

Quando usar Prim:
- É geralmente mais eficiente para grafos densos (onde $|E|$ é próximo de $V^2$) se implementado com heap de Fibonacci,
  ou mesmo com heap binário ($O(E \log V)$ pode ser melhor que $O(E \log E)$ de Kruskal se $\log V < \log E$).
- Se o grafo já é dado como listas de adjacência.

Teste de Mesa (para o primeiro exemplo do `main` com 4 vértices, começando em 0):
Vértices: 0,1,2,3. Arestas: (0,1,10), (0,2,6), (0,3,5), (1,3,15), (2,3,4)
VerticeInicial = 0.

verticesInfo:
v[0]: id=0, chave=inf, pai=-1, naAGM=F
v[1]: id=1, chave=inf, pai=-1, naAGM=F
v[2]: id=2, chave=inf, pai=-1, naAGM=F
v[3]: id=3, chave=inf, pai=-1, naAGM=F

1. v[0].chave = 0.
2. FilaPrioridade Q = [v[0](ch=0), v[1](ch=inf), v[2](ch=inf), v[3](ch=inf)]
   AGM_arestas = []

3. Loop 1:
   uInfo = Q.poll() -> v[0](ch=0). uInfo.naAGM = T.
   Pai é -1, não adiciona aresta.
   Vizinhos de 0:
   - (0,1,10): v[1].naAGM=F. 10 < v[1].chave(inf). Sim.
     Q.remove(v[1]). v[1].chave=10, v[1].pai=0. Q.add(v[1]).
     Q = [v[1](ch=10), v[2](ch=inf), v[3](ch=inf)] (a ordem exata depende da implementação da PQ)
   - (0,2,6): v[2].naAGM=F. 6 < v[2].chave(inf). Sim.
     Q.remove(v[2]). v[2].chave=6, v[2].pai=0. Q.add(v[2]).
     Q = [v[2](ch=6), v[1](ch=10), v[3](ch=inf)] (v[2] agora tem menor chave)
   - (0,3,5): v[3].naAGM=F. 5 < v[3].chave(inf). Sim.
     Q.remove(v[3]). v[3].chave=5, v[3].pai=0. Q.add(v[3]).
     Q = [v[3](ch=5), v[2](ch=6), v[1](ch=10)] (v[3] agora tem menor chave)

4. Loop 2:
   uInfo = Q.poll() -> v[3](ch=5). uInfo.naAGM = T.
   Pai é 0. AGM_arestas.add("(0 -- 3, peso: 5)"). PesoTotal=5.
   Vizinhos de 3:
   - (3,0,5): v[0].naAGM=T. Ignora.
   - (3,1,15): v[1].naAGM=F. 15 < v[1].chave(10). Não.
   - (3,2,4): v[2].naAGM=F. 4 < v[2].chave(6). Sim.
     Q.remove(v[2]). v[2].chave=4, v[2].pai=3. Q.add(v[2]).
     Q = [v[2](ch=4), v[1](ch=10)] (v[2] agora tem menor chave)

5. Loop 3:
   uInfo = Q.poll() -> v[2](ch=4). uInfo.naAGM = T.
   Pai é 3. AGM_arestas.add("(3 -- 2, peso: 4)"). PesoTotal=5+4=9.
   Vizinhos de 2:
   - (2,0,6): v[0].naAGM=T. Ignora.
   - (2,3,4): v[3].naAGM=T. Ignora.
   Q = [v[1](ch=10)]

6. Loop 4:
   uInfo = Q.poll() -> v[1](ch=10). uInfo.naAGM = T.
   Pai é 0. AGM_arestas.add("(0 -- 1, peso: 10)"). PesoTotal=9+10=19.
   Vizinhos de 1:
   - (1,0,10): v[0].naAGM=T. Ignora.
   - (1,3,15): v[3].naAGM=T. Ignora.
   Q = []

7. Q vazia. Fim.
Resultado: Arestas (0,3,5), (3,2,4), (0,1,10). Peso total 19.
(Mesmo resultado de Kruskal, como esperado).
*/