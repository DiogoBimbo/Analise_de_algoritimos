// Definição mínima de Grafo.Vertice para o MinHeap funcionar isoladamente (se necessário)
// Se estiver usando junto com os códigos de Dijkstra/Bellman-Ford/Prim, esta definição
// seria a que está naquelas classes (e a classe Grafo também).
class Grafo { // Classe container para Vertice, se não for usada a dos outros algoritmos
    static class Vertice {
        int indice;    // Identificador único do vértice
        int distancia; // Chave usada para a prioridade no MinHeap
        // Outros campos como 'nome', 'predecessor' podem existir dependendo do contexto.

        public Vertice(int indice, int distanciaInicial) {
            this.indice = indice;
            this.distancia = distanciaInicial;
        }
        // Simples construtor se só o índice for relevante e a distância for setada depois
        public Vertice(int indice) {
            this.indice = indice;
            this.distancia = Integer.MAX_VALUE; // Padrão para algoritmos de caminho mínimo
        }
    }
}


import java.util.HashMap;
import java.util.Map;
import java.util.Arrays; // Para o main de teste

// Implementação manual de uma fila de prioridade usando Min-Heap
// Especialmente útil para algoritmos em grafos como Dijkstra e Prim,
// onde a prioridade de um vértice é sua distância estimada da fonte/árvore.
public class MinHeap {
    private Grafo.Vertice[] A; // Array do heap, onde A[0] não é usado e o heap começa em A[1].
    private int heapSize;      // Número atual de elementos no heap.

    // Mapeia o 'indice' de um Grafo.Vertice para sua posição atual (índice) dentro do array A do heap.
    // Essencial para a operação decreaseKey, para encontrar rapidamente um vértice no heap.
    private Map<Integer, Integer> posicao;

    // Construtor: Recebe um array de vértices e constrói um Min-Heap.
    // A prioridade de cada vértice é dada pelo seu atributo 'distancia'.
    public MinHeap(Grafo.Vertice[] vertices) {
        this.heapSize = vertices.length;
        this.A = new Grafo.Vertice[heapSize + 1]; // Aloca espaço para o heap (índice 1 a heapSize).
        this.posicao = new HashMap<>();

        // Copia os vértices para o array do heap e preenche o mapa de posições.
        for (int i = 0; i < heapSize; i++) {
            A[i + 1] = vertices[i];                 // Vértices são colocados de A[1] a A[heapSize].
            posicao.put(vertices[i].indice, i + 1); // Armazena que o vértice com 'vertices[i].indice' está em A[i+1].
        }

        // Constrói o Min-Heap, rearranjando os elementos em A para satisfazer a propriedade de Min-Heap.
        buildMinHeap();
    }

    // Retorna o índice do nó pai de 'i' no heap.
    private int parent(int i) { return i / 2; }

    // Retorna o índice do filho esquerdo de 'i'.
    private int left(int i) { return 2 * i; }

    // Retorna o índice do filho direito de 'i'.
    private int right(int i) { return 2 * i + 1; }

    // Troca dois elementos (Grafo.Vertice) nas posições 'i' e 'j' do array A.
    // Também atualiza suas posições no mapa 'posicao'.
    private void swap(int i, int j) {
        Grafo.Vertice temp = A[i];
        A[i] = A[j];
        A[j] = temp;

        // Atualiza o mapa 'posicao' para refletir as novas localizações dos vértices trocados.
        posicao.put(A[i].indice, i);
        posicao.put(A[j].indice, j);
    }

    // Procedimento Min-Heapify:
    // Garante que a subárvore com raiz no índice 'i' seja um Min-Heap.
    // Assume que as subárvores esquerda e direita de 'i' já são Min-Heaps.
    private void minHeapify(int i) {
        int l = left(i);
        int r = right(i);
        int smallest = i; // Assume inicialmente que 'i' é o menor.

        // Compara A[i] com seu filho esquerdo A[l].
        // Se l <= heapSize (filho esquerdo existe) e A[l].distancia < A[smallest].distancia,
        // então o filho esquerdo é o menor até agora.
        if (l <= heapSize && A[l].distancia < A[smallest].distancia) smallest = l;
        
        // Compara o 'smallest' atual com o filho direito A[r].
        // Se r <= heapSize (filho direito existe) e A[r].distancia < A[smallest].distancia,
        // então o filho direito é o menor dos três.
        if (r <= heapSize && A[r].distancia < A[smallest].distancia) smallest = r;

        // Se 'smallest' não for 'i' (um dos filhos é menor que o pai),
        // a propriedade de Min-Heap foi violada em 'i'.
        if (smallest != i) {
            swap(i, smallest); // Troca A[i] com A[smallest].
            minHeapify(smallest); // Chama recursivamente para a subárvore afetada.
        }
    }

    // Constrói um Min-Heap a partir dos elementos no array A.
    // Aplica minHeapify de baixo para cima, começando pelo último nó que não é folha (heapSize / 2).
    private void buildMinHeap() {
        for (int i = heapSize / 2; i >= 1; i--) {
            minHeapify(i);
        }
    }

    // Verifica se o heap (fila de prioridade) está vazio.
    public boolean isEmpty() {
        return heapSize < 1;
    }

    // Remove e retorna o vértice com a menor 'distancia' (o elemento na raiz do Min-Heap).
    public Grafo.Vertice extractMin() {
        if (heapSize < 1) throw new RuntimeException("Heap underflow: Heap vazio");

        Grafo.Vertice min = A[1]; // O menor elemento é a raiz A[1].
        
        // Remove 'min' do mapa de posições, pois ele não está mais no heap.
        posicao.remove(min.indice);

        // Se houver mais elementos, move o último elemento do heap (A[heapSize]) para a raiz A[1].
        if (heapSize > 1) { // Só faz sentido mover se houver mais de um elemento
            A[1] = A[heapSize];
            posicao.put(A[1].indice, 1); // Atualiza a posição do elemento movido.
        }
        // Else, se heapSize era 1, A[1] já foi pego por 'min', e o heap fica vazio.
        
        heapSize--; // Decrementa o tamanho do heap.
        
        // Se o heap ainda não estiver vazio após a remoção,
        // restaura a propriedade de Min-Heap chamando minHeapify na nova raiz A[1].
        if (heapSize > 0) { // Só chama minHeapify se o heap não ficou vazio
             minHeapify(1);
        }
        
        return min; // Retorna o vértice com a menor distância.
    }

    // Operação Decrease-Key:
    // Atualiza a prioridade (distancia) de um vértice 'v' que já está no heap,
    // assumindo que a nova distância é menor que a anterior.
    // Após atualizar a distância de 'v', este método "sobe" 'v' no heap
    // até que a propriedade de Min-Heap seja restaurada.
    public void decreaseKey(Grafo.Vertice v, int novaDistancia) {
        // Encontra a posição atual 'i' de 'v' no array A usando o mapa 'posicao'.
        // Se o vértice não estiver no mapa, ou se sua posição for inválida, não faz nada
        // (ou poderia lançar um erro).
        if (!posicao.containsKey(v.indice)) return; // Vértice não está (ou não estava mais) no heap
        
        int i = posicao.get(v.indice);

        // Verifica se a posição é válida e se a nova distância é realmente menor.
        if (i > heapSize || novaDistancia >= A[i].distancia) {
            // Se a nova distância não for menor, ou o item não está efetivamente no heap,
            // não é uma operação "decreaseKey" válida ou nada precisa ser feito.
            // Poderia até atualizar se for igual, mas não afetaria a posição por "subida".
            // Se for maior, seria um "increaseKey", que requereria "descida" (minHeapify).
            // Aqui, estamos focados no decreaseKey.
             if (novaDistancia < A[i].distancia) A[i].distancia = novaDistancia; // Atualiza se for menor
             else return; // Se não for estritamente menor, não há "subida" a fazer
        }
        
        A[i].distancia = novaDistancia; // Atualiza a distância do vértice no heap.

        // "Sobe" o vértice A[i] no heap enquanto ele for menor que seu pai
        // e não for a raiz (i > 1).
        while (i > 1 && A[parent(i)].distancia > A[i].distancia) {
            swap(i, parent(i)); // Troca A[i] com seu pai A[parent(i)].
            i = parent(i);      // Move o foco para a nova posição do vértice (a do pai anterior).
        }
    }

    // Verifica se um vértice (identificado pelo seu objeto Grafo.Vertice)
    // ainda está presente no heap (ou seja, não foi extraído).
    public boolean contains(Grafo.Vertice v) {
        // Verifica se o 'indice' do vértice está no mapa 'posicao' E
        // se sua posição mapeada está dentro dos limites atuais do heapSize.
        // Isso é importante porque um vértice pode ter sido extraído (heapSize diminuído),
        // mas sua entrada no mapa 'posicao' só é removida em extractMin
        // ou se o vértice que o substituiu na posição tem o mesmo 'indice' (improvável com índices únicos).
        // A verificação de `posicao.get(v.indice) <= heapSize` é crucial.
        return posicao.containsKey(v.indice) && posicao.get(v.indice) <= heapSize && A[posicao.get(v.indice)] == v;
    }
    
    // Método de teste
    public static void main(String[] args) {
        // Cria alguns vértices de exemplo. Em um cenário real, viriam de um objeto Grafo.
        Grafo.Vertice v0 = new Grafo.Vertice(0); v0.distancia = 10;
        Grafo.Vertice v1 = new Grafo.Vertice(1); v1.distancia = 5;
        Grafo.Vertice v2 = new Grafo.Vertice(2); v2.distancia = 20;
        Grafo.Vertice v3 = new Grafo.Vertice(3); v3.distancia = 8;
        Grafo.Vertice v4 = new Grafo.Vertice(4); v4.distancia = 2;

        Grafo.Vertice[] vertices = {v0, v1, v2, v3, v4};
        System.out.println("Vértices originais (indice:distancia):");
        for(Grafo.Vertice v : vertices) System.out.print(v.indice + ":" + v.distancia + "  ");
        System.out.println();

        MinHeap minHeap = new MinHeap(vertices);
        System.out.println("Heap (A[1]..A[heapSize]) após buildMinHeap:");
        for(int k=1; k <= minHeap.heapSize; k++) System.out.print(minHeap.A[k].indice + ":" + minHeap.A[k].distancia + "  ");
        System.out.println();


        System.out.println("\nExtraindo elementos:");
        while(!minHeap.isEmpty()){
            Grafo.Vertice minV = minHeap.extractMin();
            System.out.println("Extraído: " + minV.indice + " (dist: " + minV.distancia + ")");
            if (!minHeap.isEmpty()) {
                 System.out.print("Heap restante: ");
                 for(int k=1; k <= minHeap.heapSize; k++) System.out.print(minHeap.A[k].indice + ":" + minHeap.A[k].distancia + "  ");
                 System.out.println();
            } else {
                System.out.println("Heap ficou vazio.");
            }
        }

        System.out.println("\nTestando decreaseKey:");
        // Recriar o heap para o teste de decreaseKey
        v0.distancia = 10; v1.distancia = 5; v2.distancia = 20; v3.distancia = 8; v4.distancia = 100; // v4 com dist alta
        Grafo.Vertice[] vertices2 = {v0, v1, v2, v3, v4};
        MinHeap minHeap2 = new MinHeap(vertices2);
        System.out.println("Heap inicial para decreaseKey:");
        for(int k=1; k <= minHeap2.heapSize; k++) System.out.print(minHeap2.A[k].indice + ":" + minHeap2.A[k].distancia + "  ");
        System.out.println();
        
        System.out.println("Contains v4 (id 4)? " + minHeap2.contains(v4));

        System.out.println("Diminuindo chave de v4 (id 4) para 1...");
        // v4 é o vértice com índice 4. Acessamos o objeto diretamente.
        minHeap2.decreaseKey(v4, 1); // v4 é o objeto Grafo.Vertice
                                     // Se v4 não fosse acessível diretamente, precisaríamos de
                                     // um getVerticePeloIndice(indice) a partir do array original
                                     // que foi usado para construir o heap.

        System.out.println("Heap após decreaseKey(v4, 1):");
        for(int k=1; k <= minHeap2.heapSize; k++) System.out.print(minHeap2.A[k].indice + ":" + minHeap2.A[k].distancia + "  ");
        System.out.println();
        
        System.out.println("Extraindo o novo mínimo:");
        Grafo.Vertice novoMin = minHeap2.extractMin();
        System.out.println("Extraído: " + novoMin.indice + " (dist: " + novoMin.distancia + ")"); // Deve ser v4 com dist 1
    }
}

/*
Uma Fila de Prioridade é uma estrutura de dados abstrata que armazena elementos, cada um
associado a uma "prioridade" (ou "chave"). As operações principais são a inserção de um
elemento e a extração do elemento com a maior (ou menor, dependendo da implementação) prioridade.

Heaps são uma forma eficiente e comum de implementar filas de prioridade.
- Um Min-Heap é usado para implementar uma fila onde o elemento com o *menor* valor de chave
  é extraído (Min-Priority Queue).
- Um Max-Heap é usado para uma fila onde o elemento com o *maior* valor de chave é extraído
  (Max-Priority Queue).

O código `MinHeap` fornecido implementa uma Min-Priority Queue. A "prioridade" de um
`Grafo.Vertice` é o valor do seu atributo `distancia`.

Operações Chave de uma Min-Priority Queue (implementada com Min-Heap):

1.  `BUILD-MIN-HEAP(A)`:
    - Constrói um Min-Heap a partir de um array de elementos. Semelhante ao `BUILD-MAX-HEAP`,
      mas usa `MIN-HEAPIFY`.
    - Complexidade: $O(n)$, onde $n$ é o número de elementos.

2.  `MIN-HEAPIFY(A, i, heapSize)`:
    - Restaura a propriedade de Min-Heap no nó 'i', assumindo que seus filhos já são raízes
      de Min-Heaps. Compara A[i] com seus filhos e, se A[i] não for o menor, troca-o com o
      menor filho e chama `MIN-HEAPIFY` recursivamente na subárvore afetada.
    - Complexidade: $O(\log n)$ (altura do heap).

3.  `HEAP-EXTRACT-MIN(A, heapSize)`:
    - Remove e retorna o elemento com a menor chave (a raiz A[1] do Min-Heap).
    - O elemento A[1] é salvo.
    - O último elemento do heap (A[heapSize]) é movido para A[1].
    - O tamanho do heap (`heapSize`) é decrementado.
    - `MIN-HEAPIFY(A, 1, heapSize)` é chamado para restaurar a propriedade de Min-Heap na raiz.
    - Complexidade: $O(\log n)$.

4.  `HEAP-DECREASE-KEY(A, i, novaChave)` (ou `HEAP-DECREASE-KEY(A, elemento, novaChave)`):
    - Diminui o valor da chave de um elemento 'i' (ou um 'elemento' específico) para `novaChave`,
      que deve ser menor ou igual à chave atual.
    - Como a chave diminuiu, o elemento pode precisar "subir" na árvore para manter a
      propriedade de Min-Heap.
    - O elemento em A[i] (ou na posição do 'elemento') tem sua chave atualizada.
    - Ele é comparado com seu pai. Se for menor que o pai, eles são trocados. Esse processo
      de comparação e troca com o pai continua subindo na árvore até que o elemento
      não seja menor que seu pai, ou até que ele se torne a raiz.
    - Complexidade: $O(\log n)$ (altura do heap).
    - No código fornecido (`MinHeap.java`), a operação `decreaseKey(Grafo.Vertice v, int novaDistancia)`
      usa um mapa `posicao` para encontrar rapidamente a posição atual do vértice `v` no array do heap.
      Isso é crucial porque, em algoritmos como Dijkstra, não sabemos o índice exato do vértice no heap,
      apenas o objeto do vértice em si.

5.  `MIN-HEAP-INSERT(A, chave, heapSize)` (Não explicitamente no código `MinHeap` como uma função separada,
    mas a lógica é parte da construção inicial e poderia ser uma função):
    - Aumenta `heapSize`.
    - Coloca o novo elemento com uma chave temporariamente "infinita" em A[heapSize].
    - Chama `HEAP-DECREASE-KEY(A, heapSize, chave_real)` para colocar o elemento na posição correta
      com sua chave real.
    - Complexidade: $O(\log n)$.

Uso em Algoritmos de Grafos:
Min-Priority Queues são fundamentais em algoritmos como:
- **Dijkstra:** Para selecionar o vértice não visitado com a menor distância estimada da origem.
  A operação `DECREASE-KEY` é usada quando o relaxamento de uma aresta encontra um caminho
  mais curto para um vértice.
- **Prim (para AGM):** Para selecionar a aresta de menor peso que conecta um vértice na AGM
  em construção a um vértice fora dela. A "chave" de um vértice fora da AGM é o peso da
  aresta mais leve que o conecta à AGM. `DECREASE-KEY` é usado quando uma aresta mais leve
  é encontrada para conectar um vértice à AGM.

O mapa `posicao` no código `MinHeap` é uma otimização importante para a operação `DECREASE-KEY`.
Sem ele, para encontrar a posição de um vértice no array do heap para atualizar sua chave,
seria necessário uma busca linear ($O(n)$), o que degradaria a performance de `DECREASE-KEY`
e, consequentemente, dos algoritmos que dependem dela (como Dijkstra e Prim). Com o mapa,
a localização é $O(1)$ em média.
*/