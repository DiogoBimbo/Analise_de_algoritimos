import java.util.*;

// Classe Aresta: Representa uma aresta ponderada no grafo.
// Implementa Comparable para permitir a ordenação das arestas por peso.
class ArestaKruskal implements Comparable<ArestaKruskal>{ // Renomeado para evitar conflito
    int u, v, peso; // u - vértice de origem da aresta; v - vértice de destino; peso - custo da aresta

    ArestaKruskal(int u, int v, int peso){
        this.u = u;
        this.v = v;
        this.peso = peso;
    }

    // Método compareTo: Define o critério de comparação entre duas arestas.
    // A comparação é baseada no peso da aresta, essencial para o algoritmo de Kruskal.
    @Override
    public int compareTo(ArestaKruskal outraAresta){
        return Integer.compare(this.peso, outraAresta.peso);
    }
    // Exemplo: se this.peso < outraAresta.peso, retorna < 0
    //          se this.peso == outraAresta.peso, retorna 0
    //          se this.peso > outraAresta.peso, retorna > 0
}

// Classe UnionFind: Implementa a estrutura de dados Union-Find (ou Disjoint Set Union - DSU).
// Esta estrutura é usada para manter o controle dos conjuntos de vértices conectados
// e para detectar eficientemente se a adição de uma aresta forma um ciclo.
class UnionFindKruskal{ // Renomeado para evitar conflito
    int[] representantes; // Array onde representantes[i] armazena o pai/representante do elemento i.
                          // Se representantes[i] == i, então i é o representante do seu conjunto.

    // Construtor: Inicializa a estrutura Union-Find.
    // n: número de vértices do grafo.
    public UnionFindKruskal(int n){
        representantes = new int[n];
        // Operação Make-Set para cada vértice:
        // Inicialmente, cada vértice 'i' está em seu próprio conjunto, sendo seu próprio representante.
        for(int i=0; i < n; i++){
            representantes[i] = i;
        }
    }

    // Método find (Find-Set): Encontra o representante do conjunto ao qual o elemento 'x' pertence.
    // Percorre a cadeia de pais até encontrar um elemento que é seu próprio pai (o representante).
    // Esta versão não implementa compressão de caminho, mas é funcional.
    public int find(int x){
        // Enquanto 'x' não for o seu próprio representante (ou seja, representantes[x] != x),
        // suba na árvore de representantes.
        while(representantes[x] != x){
            // Opcional: compressão de caminho (path compression) para otimizar futuras buscas:
            // representantes[x] = representantes[representantes[x]]; // Faz x apontar para seu avô.
            x = representantes[x];
        }
        return x; // 'x' agora é o representante do conjunto.
    }
    // Teste de Mesa para Find-Set (sem compressão de caminho):
    // Exemplo: representantes = [0, 0, 2, 2, 3] (índices 0..4)
    //   - find(0): representantes[0]==0. Retorna 0.
    //   - find(1): representantes[1]!=1 (é 0). x=0. representantes[0]==0. Retorna 0.
    //   - find(2): representantes[2]==2. Retorna 2.
    //   - find(3): representantes[3]!=3 (é 2). x=2. representantes[2]==2. Retorna 2.
    //   - find(4): representantes[4]!=4 (é 3). x=3. representantes[3]!=3 (é 2). x=2. representantes[2]==2. Retorna 2.

    // Método union: Une os conjuntos que contêm os elementos 'x' e 'y'.
    // Faz com que o representante de um conjunto aponte para o representante do outro.
    public void union(int x, int y){
        int representanteX = find(x); // Encontra o representante do conjunto de x.
        int representanteY = find(y); // Encontra o representante do conjunto de y.

        // Se 'x' e 'y' não estiverem já no mesmo conjunto (ou seja, seus representantes são diferentes),
        // une os conjuntos fazendo o representante de Y apontar para o representante de X.
        // (A escolha de quem aponta para quem pode ser otimizada com "union by rank" ou "union by size").
        if (representanteX != representanteY) {
            representantes[representanteY] = representanteX;
        }
    }
}

public class Kruskal{
    // Método principal do algoritmo de Kruskal.
    // n: número de vértices no grafo.
    // arestas: lista de todas as arestas do grafo.
    // Retorna uma lista de arestas que compõem a Árvore Geradora Mínima (AGM).
    public List<ArestaKruskal> kruskal(int n, List<ArestaKruskal> arestas){
        // Passo 1: Ordenar todas as arestas do grafo em ordem não decrescente de peso.
        // A classe ArestaKruskal implementa Comparable, então Collections.sort() usa o peso.
        Collections.sort(arestas);

        // Passo 2: Inicializar a estrutura Union-Find.
        // Cada vértice começa em seu próprio conjunto.
        UnionFindKruskal uf = new UnionFindKruskal(n);
        
        // Lista para armazenar as arestas da AGM resultante.
        List<ArestaKruskal> arvoreResultante = new ArrayList<>();
        int numeroDeArestasNaAGM = 0;

        // Passo 3: Iterar sobre as arestas ordenadas.
        for(ArestaKruskal aresta : arestas){
            // Para cada aresta (u,v) com peso 'p':
            // Verificar se os vértices 'u' e 'v' pertencem a conjuntos diferentes.
            // Se find(u) != find(v), adicionar a aresta (u,v) à AGM não forma um ciclo.
            if(uf.find(aresta.u) != uf.find(aresta.v)){
                // Adicionar a aresta à AGM.
                arvoreResultante.add(aresta);
                numeroDeArestasNaAGM++;
                
                // Unir os conjuntos de 'u' e 'v', indicando que agora estão conectados.
                uf.union(aresta.u, aresta.v);

                // Otimização: Uma AGM com 'n' vértices terá exatamente 'n-1' arestas.
                // Se já encontramos 'n-1' arestas, podemos parar.
                if (numeroDeArestasNaAGM == n - 1) {
                    break;
                }
            }
        }
        // Se o grafo original não for conectado, arvoreResultante conterá uma floresta geradora mínima,
        // e numeroDeArestasNaAGM será menor que n-1.
        return arvoreResultante;
    }

    public static void main(String[] args){
        int n = 4; // Número de vértices (0, 1, 2, 3)
        List<ArestaKruskal> arestas = new ArrayList<>(Arrays.asList(
            new ArestaKruskal(0,1,10),
            new ArestaKruskal(0,2,6),
            new ArestaKruskal(0,3,5),
            new ArestaKruskal(1,3,15),
            new ArestaKruskal(2,3,4)
        ));
        // Grafo de exemplo:
        // Vértices: 0, 1, 2, 3
        // Arestas (peso):
        // (0,1,10)
        // (0,2,6)
        // (0,3,5)
        // (1,3,15)
        // (2,3,4)

        Kruskal algoritmoKruskal = new Kruskal();
        List<ArestaKruskal> agm = algoritmoKruskal.kruskal(n, arestas);
        
        int pesoTotalAGM = 0;
        System.out.println("Arestas da Árvore Geradora Mínima (Kruskal):");
        for(ArestaKruskal a : agm){
            System.out.printf("(%d -- %d, peso: %d)\n", a.u, a.v, a.peso);
            pesoTotalAGM += a.peso;
        }
        System.out.println("Peso total da AGM: " + pesoTotalAGM); // Esperado: 5+4+6 = 15 (se n=4)
                                                              // Para o exemplo dado: (2,3,4), (0,3,5), (0,2,6) -> 4+5+6 = 15
                                                              // Arestas ordenadas:
                                                              // (2,3,4) -> find(2)=2, find(3)=3. Add. union(2,3). R=[0,1,2,2]. AGM=[(2,3,4)]
                                                              // (0,3,5) -> find(0)=0, find(3)=2. Add. union(0,2). R=[0,1,0,2]. AGM=[(2,3,4),(0,3,5)]
                                                              // (0,2,6) -> find(0)=0, find(2)=0. Ciclo! Ignora.
                                                              // (0,1,10) -> find(0)=0, find(1)=1. Add. union(0,1). R=[0,0,0,2]. AGM=[(2,3,4),(0,3,5),(0,1,10)]
                                                              // Peso: 4+5+10 = 19.
    }
}

/*

O Algoritmo de Kruskal é um método guloso para encontrar uma Árvore Geradora Mínima (AGM)
em um grafo conectado, não direcionado e ponderado. Se o grafo não for conectado, ele
encontra uma Floresta Geradora Mínima (uma AGM para cada componente conectado).

Princípios Fundamentais:
1.  **Estratégia Gulosa:** A ideia é construir a AGM adicionando arestas uma por uma,
    sempre escolhendo a aresta de menor peso disponível que não forme um ciclo com
    as arestas já selecionadas.

2.  **Ordenação de Arestas:** O primeiro passo crucial é ordenar todas as arestas do grafo
    em ordem não decrescente de seus pesos.

3.  **Detecção de Ciclos com Union-Find:**
    - Para determinar eficientemente se a adição de uma aresta $(u,v)$ forma um ciclo,
      Kruskal utiliza uma estrutura de dados chamada Union-Find (ou Disjoint Set Union - DSU).
    - Inicialmente, cada vértice do grafo é considerado um conjunto disjunto separado
      (operação `MAKE-SET` para cada vértice).
    - Ao considerar uma aresta $(u,v)$ (da lista ordenada):
        - Se $u$ e $v$ já pertencem ao mesmo conjunto (verificado pela operação `FIND-SET(u) == FIND-SET(v)`),
          adicionar a aresta $(u,v)$ criaria um ciclo. Portanto, a aresta é descartada.
        - Se $u$ e $v$ pertencem a conjuntos diferentes, a aresta $(u,v)$ é segura para ser
          adicionada à AGM. Após a adição, os conjuntos de $u$ e $v$ são fundidos
          (operação `UNION(u,v)`), indicando que esses dois componentes (anteriormente separados)
          agora estão conectados.

4.  **Construção da AGM:** A AGM é formada pelo conjunto de arestas que foram adicionadas.
    O processo continua até que $|V|-1$ arestas tenham sido adicionadas à AGM (onde $|V|$ é
    o número de vértices), ou até que todas as arestas tenham sido consideradas.
    Uma árvore com $|V|$ vértices sempre terá exatamente $|V|-1$ arestas.

Passos do Algoritmo:
1.  Crie um conjunto A vazio, que armazenará as arestas da AGM.
2.  Para cada vértice $v \in G.V$, crie um conjunto disjunto contendo apenas $v$ (operação `MAKE-SET(v)`).
    (Isso é feito implicitamente ao inicializar a estrutura UnionFind).
3.  Ordene as arestas $G.E$ em ordem não decrescente de peso $w$.
4.  Para cada aresta $(u,v) \in G.E$, na ordem de pesos crescentes:
    a. Se `FIND-SET(u)` $\neq$ `FIND-SET(v)`:
        i. Adicione a aresta $(u,v)$ ao conjunto A.
        ii. `UNION(u,v)`.
5.  Retorne o conjunto A.

Corretude:
A corretude do algoritmo de Kruskal baseia-se na "propriedade de corte". De forma simplificada,
se pegarmos qualquer corte (uma partição dos vértices em dois conjuntos disjuntos) do grafo,
e se uma aresta $(u,v)$ de peso mínimo cruza esse corte, então essa aresta $(u,v)$ deve pertencer
a alguma AGM do grafo. Kruskal, ao processar arestas em ordem de peso, sempre escolhe a aresta
mais leve que conecta dois componentes previamente desconectados, o que está alinhado com
esta propriedade.

Complexidade:
-   **Ordenação das Arestas:** Se $|E|$ é o número de arestas, a ordenação leva $O(E \log E)$.
-   **Operações Union-Find:**
    - Inicialização (MAKE-SET para cada vértice): $O(V)$.
    - O loop principal itera sobre todas as $|E|$ arestas. Dentro do loop, são realizadas
      duas operações `FIND-SET` e, no máximo, uma operação `UNION`.
    - Com uma implementação otimizada de Union-Find (usando união por rank/tamanho e
      compressão de caminho), uma sequência de $m$ operações `UNION` ou `FIND-SET`
      leva tempo quase linear, $O(m \cdot \alpha(V))$, onde $\alpha(V)$ é a função
      inversa de Ackermann, que cresce muito lentamente e é praticamente constante
      (menor que 5) para todos os valores práticos de $V$.
    - Portanto, as operações Union-Find para $|E|$ arestas levam aproximadamente $O(E \cdot \alpha(V))$.
-   **Complexidade Total:** Dominada pela ordenação das arestas, resultando em $O(E \log E)$.
    Como $|E|$ pode ser até $O(V^2)$ em grafos densos, isso também pode ser escrito como $O(E \log V)$
    (já que $\log E \approx \log V^2 = 2 \log V$).

Quando usar Kruskal:
- É geralmente eficiente para grafos esparsos (onde $|E|$ é próximo de $|V|$), pois a
  complexidade depende mais do número de arestas do que de vértices (além do $\log E$ ou $\log V$).
- A implementação é relativamente simples se uma boa biblioteca Union-Find estiver disponível.

Teste de Mesa para o `main` fornecido:
n = 4. Vértices: 0, 1, 2, 3.
Arestas: (0,1,10), (0,2,6), (0,3,5), (1,3,15), (2,3,4)

1. Ordenar Arestas:
   - (2,3,4)
   - (0,3,5)
   - (0,2,6)
   - (0,1,10)
   - (1,3,15)

2. UnionFind: R = [0,1,2,3] (representantes iniciais)
   AGM = []

3. Iterar:
   a. Aresta (2,3,4):
      find(2)=2, find(3)=3. São diferentes.
      AGM.add((2,3,4)).
      union(2,3) -> R[find(3)] = find(2) -> R[3]=2.
      R = [0,1,2,2]. AGM = [(2,3,4)]

   b. Aresta (0,3,5):
      find(0)=0, find(3)=2 (pois R[3]=2, find(2)=2). São diferentes.
      AGM.add((0,3,5)).
      union(0,3) -> R[find(3)] = find(0) -> R[2]=0.
      R = [0,1,0,2]. AGM = [(2,3,4), (0,3,5)]

   c. Aresta (0,2,6):
      find(0)=0, find(2)=0 (pois R[2]=0, find(0)=0). São iguais! Forma ciclo.
      Ignora.
      R = [0,1,0,2]. AGM = [(2,3,4), (0,3,5)]

   d. Aresta (0,1,10):
      find(0)=0, find(1)=1. São diferentes.
      AGM.add((0,1,10)).
      union(0,1) -> R[find(1)] = find(0) -> R[1]=0.
      R = [0,0,0,2]. AGM = [(2,3,4), (0,3,5), (0,1,10)]
      Número de arestas na AGM = 3. Como n=4, n-1=3. Podemos parar.

   (Se não parássemos)
   e. Aresta (1,3,15):
      find(1)=0, find(3)=0 (pois R[3]=2 -> R[2]=0). São iguais! Forma ciclo.
      Ignora.

Resultado: AGM = [(2,3,4), (0,3,5), (0,1,10)]
Peso Total = 4 + 5 + 10 = 19.
*/