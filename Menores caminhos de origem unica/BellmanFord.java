import java.util.*;

// Classe que representa o grafo
class GrafoBellmanFord { // Renomeado para evitar conflito

    // Vetor que guarda os vértices do grafo
    private final Vertice[] vertices;
    // Lista de adjacência: cada índice contém a lista de arestas do vértice correspondente
    private List<List<Aresta>> adjacencias;

    // Construtor do grafo que recebe os nomes dos vértices
    GrafoBellmanFord(String[] nomesVertices) { // Exemplo: {S, T, Y, X, Z}
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
        int indice; // indice do vértice no vetor
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
        int destino; // indice do vértice de destino
        int peso;   // Peso da aresta

        // Construtor de aresta
        Aresta(int destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }
}

// Classe que implementa o algoritmo de BellmanFord
public class BellmanFord {

    // Método que executa o algoritmo de BellmanFord a partir do vértice de origem 's'
    // Retorna true se não houver ciclos negativos alcançáveis da origem, false caso contrário.
    public boolean executar(GrafoBellmanFord g, int s) {
        // Passo 1: Inicializar o grafo.
        // Define a distância da origem 's' para 0 e para todos os outros vértices como infinito.
        // Define todos os predecessores como nulos.
        inicializarFonteUnica(g, s);

        // Passo 2: Relaxar repetidamente todas as arestas.
        // Um caminho mais curto em um grafo com |V| vértices pode ter no máximo |V|-1 arestas
        // (se não houver ciclos de peso negativo).
        // Portanto, repetimos o processo de relaxamento |V|-1 vezes.
        // complexidade do loop externo: O(V)
        for (int i = 0; i < g.quantidadeVertices() - 1; i++) {
            boolean algumaDistanciaMudouNestaIteracao = false;
            // Para cada vértice 'u' no grafo...
            for (GrafoBellmanFord.Vertice u : g.getVertices()) {
                // Se u é inalcançável, não há como relaxar suas arestas saindo dele para melhorar caminhos.
                if (u.distancia == Integer.MAX_VALUE) {
                    continue;
                }
                // Para cada aresta (u,v) saindo de 'u'...
                // complexidade do loop interno (sobre todas as arestas): O(E)
                for (GrafoBellmanFord.Aresta a : g.vizinhos(u.indice)) {
                    GrafoBellmanFord.Vertice v = g.getVertices()[a.destino]; // Obtém o vértice de destino 'v'.
                    // Tenta relaxar a aresta (u,v).
                    if (relaxar(u, v, a.peso)) {
                        algumaDistanciaMudouNestaIteracao = true;
                    }
                }
            }
            // Otimização: se nenhuma distância mudou em uma iteração completa,
            // os caminhos mais curtos já foram encontrados e podemos parar cedo.
            if (!algumaDistanciaMudouNestaIteracao) {
                break;
            }
        } // Complexidade total do Passo 2: O(V * E)

        // Passo 3: Verificar a existência de ciclos de peso negativo.
        // Se após |V|-1 iterações ainda for possível relaxar alguma aresta,
        // isso indica a presença de um ciclo de peso negativo alcançável a partir da origem.
        for (GrafoBellmanFord.Vertice u : g.getVertices()) {
             if (u.distancia == Integer.MAX_VALUE) {
                continue;
            }
            for (GrafoBellmanFord.Aresta a : g.vizinhos(u.indice)) {
                GrafoBellmanFord.Vertice v = g.getVertices()[a.destino];
                // Se v.distancia ainda pode ser melhorada, então há um ciclo negativo.
                if (v.distancia > u.distancia + a.peso) {
                    // Para realmente identificar o ciclo, seria necessário rastrear os predecessores.
                    // Aqui, apenas indicamos sua presença.
                    // Poderíamos opcionalmente marcar v.distancia = -Integer.MAX_VALUE para indicar ciclo.
                    return false; // Um ciclo de peso negativo foi encontrado.
                }
            }
        }
        return true; // O grafo não possui um ciclo de peso negativo alcançável pela origem.
    }

    // Operação de Relaxamento:
    // Se o caminho atual para 'v' é mais longo do que o caminho para 'u' somado ao peso da aresta (u,v),
    // então encontramos um caminho mais curto para 'v' passando por 'u'.
    // Atualizamos a distância de 'v' e definimos 'u' como seu predecessor.
    // Retorna true se a distância foi atualizada, false caso contrário.
    private boolean relaxar(GrafoBellmanFord.Vertice u, GrafoBellmanFord.Vertice v, int pesoArestaUV) {
        // Verifica se a distância de 'v' pode ser melhorada passando por 'u'.
        // u.distancia != Integer.MAX_VALUE é crucial para evitar que 'infinito + peso' cause problemas
        // ou falsos relaxamentos a partir de nós inalcançáveis.
        if (u.distancia != Integer.MAX_VALUE && u.distancia + pesoArestaUV < v.distancia) {
            v.distancia = u.distancia + pesoArestaUV; // Atualiza a nova menor distância para 'v'.
            v.predecessor = u; // 'u' se torna o predecessor de 'v' no caminho mais curto.
            return true;
        }
        return false;
    }

    // Inicializa os atributos 'distancia' e 'predecessor' de todos os vértices.
    // Define a distância da origem 's' como 0 e dos demais como infinito (Integer.MAX_VALUE).
    // Define todos os predecessores como nulos.
    private void inicializarFonteUnica(GrafoBellmanFord g, int s) {
        for (GrafoBellmanFord.Vertice v : g.getVertices()) {
            v.distancia = Integer.MAX_VALUE;
            v.predecessor = null;
        }
        g.getVertices()[s].distancia = 0; // A distância da origem para ela mesma é 0.
    }

    // Método principal de execução/teste do algoritmo
    public static void main(String[] args) {
        // Cria vetor com os nomes dos vértices do grafo 
        String[] nomes = {"s", "t", "x", "y", "z"}; // s=0, t=1, x=2, y=3, z=4

        // Instancia um grafo com os nomes informados
        GrafoBellmanFord g = new GrafoBellmanFord(nomes);

        // Adiciona arestas ao grafo com seus respectivos pesos
        // Este é o mesmo grafo usado no exemplo de Dijkstra, que não tem ciclos negativos.
        g.adicionarAresta(0, 1, 6);  // s -> t (peso 6)
        g.adicionarAresta(0, 3, 7);  // s -> y (peso 7)
        g.adicionarAresta(1, 2, 5);  // t -> x (peso 5)
        g.adicionarAresta(1, 3, 8);  // t -> y (peso 8)
        g.adicionarAresta(1, 4, -4); // t -> z (peso -4) <--- ARESTA NEGATIVA
        g.adicionarAresta(2, 1, -2); // x -> t (peso -2) <--- ARESTA NEGATIVA
        g.adicionarAresta(3, 2, -3); // y -> x (peso -3) <--- ARESTA NEGATIVA
        g.adicionarAresta(3, 4, 9);  // y -> z (peso 9)
        g.adicionarAresta(4, 0, 2);  // z -> s (peso 2)
        g.adicionarAresta(4, 2, 7);  // z -> x (peso 7)


        // Cria uma instância do algoritmo de BellmanFord e executa a partir do vértice 's' (índice 0)
        BellmanFord bellmanFord = new BellmanFord();
        
        System.out.println("Executando Bellman-Ford (origem: s):");
        boolean semCicloNegativo = bellmanFord.executar(g, 0);

        if (semCicloNegativo) {
            System.out.println("Não foram detectados ciclos de peso negativo alcançáveis pela origem.");
            // Exibe os resultados: distância mínima e predecessor de cada vértice
            for (GrafoBellmanFord.Vertice v : g.getVertices()) {
                 System.out.printf("Vértice %s: Distância = %s, Predecessor = %s\n",
                    v.nome,
                    (v.distancia == Integer.MAX_VALUE ? "inf" : v.distancia),
                    (v.predecessor != null ? v.predecessor.nome : "Nenhum")
                );
            }
        } else {
            System.out.println("Ciclo de peso negativo detectado! As distâncias podem não ser os verdadeiros caminhos mais curtos.");
             // Opcionalmente, imprimir as distâncias mesmo assim, mas com o aviso.
            for (GrafoBellmanFord.Vertice v : g.getVertices()) {
                 System.out.printf("Vértice %s: Distância (potencialmente afetada por ciclo neg.) = %s, Predecessor = %s\n",
                    v.nome,
                    (v.distancia == Integer.MAX_VALUE ? "inf" : v.distancia),
                    (v.predecessor != null ? v.predecessor.nome : "Nenhum")
                );
            }
        }

        // Teste com ciclo negativo
        System.out.println("\n--- Teste com Ciclo Negativo ---");
        GrafoBellmanFord gComCiclo = new GrafoBellmanFord(new String[]{"a", "b", "c"}); // a=0, b=1, c=2
        gComCiclo.adicionarAresta(0, 1, 1);  // a -> b (1)
        gComCiclo.adicionarAresta(1, 2, -1); // b -> c (-1)
        gComCiclo.adicionarAresta(2, 1, -1); // c -> b (-1)  (Ciclo b-c-b com peso -2)

        semCicloNegativo = bellmanFord.executar(gComCiclo, 0);
         if (semCicloNegativo) {
            System.out.println("Não foram detectados ciclos de peso negativo.");
        } else {
            System.out.println("Ciclo de peso negativo detectado!");
        }
    }

/*

O Algoritmo de Bellman-Ford resolve o problema do caminho mais curto de uma única origem em grafos ponderados
direcionados, e sua principal característica é a capacidade de lidar com **arestas de peso negativo**.
Ele também pode detectar a presença de ciclos de peso negativo alcançáveis a partir da origem.

Princípios Fundamentais:
1.  **Relaxamento Iterativo de Todas as Arestas:** A ideia central é que um caminho mais curto de 's' a 'v'
    não pode conter mais do que $|V|-1$ arestas (onde $|V|$ é o número de vértices), assumindo que não
    existem ciclos de peso negativo. O algoritmo realiza $|V|-1$ iterações (passagens). Em cada iteração,
    ele percorre todas as arestas do grafo e tenta realizar a operação de "relaxamento".

2.  **Convergência Progressiva:**
    - Após a 1ª iteração, Bellman-Ford encontra todos os caminhos mais curtos de no máximo 1 aresta.
    - Após a 2ª iteração, encontra todos os caminhos mais curtos de no máximo 2 arestas.
    - ...
    - Após a $k$-ésima iteração, encontra todos os caminhos mais curtos de no máximo $k$ arestas.
    - Portanto, após $|V|-1$ iterações, ele terá encontrado todos os caminhos mais curtos possíveis se
      não houver ciclos de peso negativo.

3.  **Operação de Relaxamento (RELAX):** Similar à de Dijkstra. Para uma aresta $(u,v)$ com peso $w(u,v)$:
    Se $d[v] > d[u] + w(u,v)$:
        $d[v] = d[u] + w(u,v)$
        $\pi[v] = u$
    (onde $d[v]$ é a estimativa da distância de 's' a 'v', e $\pi[v]$ é o predecessor de 'v').

4.  **Detecção de Ciclos de Peso Negativo:**
    Após as $|V|-1$ iterações, se for possível realizar mais uma passagem sobre todas as arestas e
    ainda encontrar uma aresta $(u,v)$ que possa ser relaxada (ou seja, $d[v] > d[u] + w(u,v)$),
    então existe um ciclo de peso negativo no grafo que é alcançável a partir da fonte 's'.
    Isso ocorre porque, se não houvesse tais ciclos, as distâncias já teriam convergido para seus
    valores ótimos. Se um ciclo de peso negativo existe, as distâncias dos vértices no ciclo (ou
    alcançáveis a partir dele) podem ser reduzidas indefinidamente.

Passos do Algoritmo:
1.  `INITIALIZE-SINGLE-SOURCE(G, s)`:
    Para cada vértice $v \in G.V$:
        $v.distancia = \infty$
        $v.predecessor = \text{null}$
    $s.distancia = 0$

2.  Repita $|V|-1$ vezes:
    Para cada aresta $(u,v) \in G.E$:
        $\text{RELAX}(u, v, w(u,v))$

3.  Para cada aresta $(u,v) \in G.E$:
    Se $v.distancia > u.distancia + w(u,v)$:
        Retorne `FALSE` (ciclo de peso negativo detectado)

4.  Retorne `TRUE` (não há ciclos de peso negativo alcançáveis, distâncias são finais)

Complexidade:
-   **Tempo:** O passo 1 leva $O(V)$. O passo 2 consiste em $|V|-1$ iterações, e dentro de cada iteração,
    todas as $|E|$ arestas são processadas (o código itera sobre vértices e depois suas arestas,
    o que equivale a iterar sobre todas as arestas). Portanto, o passo 2 é $O(V \cdot E)$.
    O passo 3 (verificação de ciclo) leva $O(E)$.
    A complexidade total é $O(V \cdot E)$.
-   **Espaço:** $O(V)$ para armazenar distâncias e predecessores.

Vantagens sobre Dijkstra:
- Funciona com pesos de aresta negativos.
- Pode detectar ciclos de peso negativo.

Desvantagens em relação a Dijkstra:
- Mais lento ($O(V \cdot E)$ vs $O(E \log V)$ ou $O(V^2)$ de Dijkstra). Se os pesos são
  todos não negativos e a velocidade é crítica, Dijkstra é preferível.

Exemplo de Teste de Mesa (Grafo da Fig. 24.4(a) do Cormen, 3ª ed.):
Vértices: s(0), t(1), x(2), y(3), z(4). |V|=5. Iterações = |V|-1 = 4.
Arestas e Pesos (conforme `main`):
s->t (6), s->y (7)
t->x (5), t->y (8), t->z (-4)
x->t (-2)
y->x (-3), y->z (9)
z->s (2), z->x (7)

Inicialização (d): [0, inf, inf, inf, inf], π: [N,N,N,N,N]

Iteração 1 (relaxar todas as arestas):
(s,t,6): d[t]=6, π[t]=s
(s,y,7): d[y]=7, π[y]=s
(t,x,5): d[x]= (6+5)=11, π[x]=t (assume d[t] já atualizado nesta iteração)
(t,y,8): d[y] não melhora (7 < 6+8)
(t,z,-4): d[z]=(6-4)=2, π[z]=t
(x,t,-2): d[t] não melhora (6 < 11-2) -> Se d[x] for usado antes de ser atualizado na mesma iteração, isso muda.
          A ordem de relaxamento das arestas dentro de uma iteração é importante para o estado intermediário,
          mas o resultado final após |V|-1 iterações é o mesmo.
          Assumindo que os 'd' da iteração anterior (ou início da atual) são usados para o lado direito do relax.
          Correto é usar d[u] do estado atual da iteração.
          Estado ao final da Iteração 1 (após todas as arestas serem checadas 1x):
          d: [0, 6, 11, 7, 2] (s,t,x,y,z)
          π: [N, s,  t, s, t]

Iteração 2:
... (s,t,6), (s,y,7) -> sem mudança
... (t,x,5): d[x] (11) vs d[t]+5 (6+5=11) -> sem mudança
... (t,y,8): d[y] (7) vs d[t]+8 (6+8=14) -> sem mudança
... (t,z,-4): d[z] (2) vs d[t]-4 (6-4=2) -> sem mudança
... (x,t,-2): d[t] (6) vs d[x]-2 (11-2=9) -> sem mudança
... (y,x,-3): d[x] (11) vs d[y]-3 (7-3=4). Sim! d[x]=4, π[x]=y
... (y,z,9): d[z] (2) vs d[y]+9 (7+9=16) -> sem mudança
... (z,s,2): d[s] (0) vs d[z]+2 (2+2=4) -> sem mudança
... (z,x,7): d[x] (4) vs d[z]+7 (2+7=9) -> sem mudança
          Estado ao final da Iteração 2:
          d: [0, 6, 4, 7, 2]
          π: [N, s, y, s, t]

Iteração 3:
... (x,t,-2): d[t] (6) vs d[x]-2 (4-2=2). Sim! d[t]=2, π[t]=x
          Estado ao final da Iteração 3 (parcial, focando na mudança):
          d: [0, 2, 4, 7, 2]
          π: [N, x, y, s, t]

Iteração 4:
... (t,x,5): d[x] (4) vs d[t]+5 (2+5=7) -> sem mudança
... (t,z,-4): d[z] (2) vs d[t]-4 (2-4=-2). Sim! d[z]=-2, π[z]=t
          Estado ao final da Iteração 4 (parcial):
          d: [0, 2, 4, 7, -2]
          π: [N, x, y, s, t]

Verificação de Ciclo Negativo (após 4 iterações):
Percorrer todas as arestas novamente.
Ex: (x,t,-2). d[t]=2. d[x]-2 = 4-2=2.  2 > 2 é falso. Não relaxa.
Ex: (t,z,-4). d[z]=-2. d[t]-4 = 2-4=-2. -2 > -2 é falso. Não relaxa.
... Nenhuma aresta pode ser mais relaxada.

Resultado Final Esperado (conforme Fig 24.4(e) do Cormen):
d[s]=0, π[s]=N
d[t]=2, π[t]=x
d[x]=4, π[x]=y
d[y]=7, π[y]=s
d[z]=-2, π[z]=t

Este grafo não possui ciclos negativos. O algoritmo deve retornar `true`.
*/
}