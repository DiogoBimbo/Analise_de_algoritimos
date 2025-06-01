package heap;

import java.util.Arrays;

public class HeapSort {

    // Retorna o índice do nó pai de i (considerando indexação a partir de 1)
    public static int parent (int i){
        return i / 2;
    }

    // Retorna o índice do filho esquerdo de i
    public static int left(int i){
        return 2 * i;
    }

    // Retorna o índice do filho direito de i
    public static int right(int i){
        return 2 * i + 1;
    }

    // Método auxiliar para trocar dois elementos em um array
    private static void swap (int [] A, int i, int j){
        var temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }

    // Procedimento Max-Heapify:
    // Garante que a subárvore com raiz no índice 'i' seja um Max-Heap.
    // Assume que as subárvores esquerda e direita de 'i' já são Max-Heaps.
    // 'A' é o array, 'i' é o índice da raiz da subárvore, 'n' é o tamanho efetivo do heap.
    // (Os elementos do heap vão de A[1] até A[n])
    public static void maxHeapify(int [] A, int i, int n){
        // Calcula os índices dos filhos esquerdo (l) e direito (r) de 'i'.
        var l = left(i);
        var r = right(i);
        int largest; // Armazenará o índice do maior elemento entre A[i], A[l] e A[r].

        // Compara A[i] com seu filho esquerdo A[l].
        // Se l <= n (filho esquerdo existe dentro do heap) e A[l] > A[i],
        // então o filho esquerdo é, por enquanto, o maior.
        if( l <= n && A[l] > A[i])
            largest = l;
        else
            largest = i; // Caso contrário, A[i] é o maior (ou não tem filho esquerdo).

        // Compara o 'largest' atual com o filho direito A[r].
        // Se r <= n (filho direito existe) e A[r] > A[largest],
        // então o filho direito é o maior dos três.
        if (r <= n && A[r] > A[largest])
            largest = r;

        // Se 'largest' não for 'i' (ou seja, um dos filhos é maior que o pai),
        // então a propriedade de Max-Heap foi violada em 'i'.
        if(largest != i){
            swap(A, i, largest); // Troca A[i] com A[largest] para corrigir a violação.
            // Como a troca pode ter violado a propriedade de Max-Heap na subárvore
            // que agora tem A[i] (o antigo valor de A[largest]) como raiz,
            // chama maxHeapify recursivamente para essa subárvore.
            maxHeapify(A, largest, n);
        }
    }
    
    // Procedimento Build-Max-Heap:
    // Constrói um Max-Heap a partir de um array desordenado 'A'.
    // 'n' é o número de elementos no heap (A[1...n]).
    // A ideia é aplicar maxHeapify de baixo para cima, começando pelo último nó
    // que não é uma folha (e portanto, pode ter filhos). Esse nó é o pai do último elemento,
    // ou seja, A[n/2]. Os elementos de A[n/2 + 1 ... n] são todas folhas e já são heaps triviais.
    public static void buildMaxHeap(int [] A, int n){
        for(int i = n / 2; i >= 1; i--){ // Itera dos últimos pais até a raiz.
            maxHeapify(A, i, n);
        }
    }

    // Algoritmo HeapSort:
    // Ordena o array 'A' em ordem crescente.
    // Assume que o array 'A' usa o índice 0 para um valor sentinela ou não o utiliza,
    // e os dados a serem ordenados estão de A[1] até A[A.length - 1].
    public static void heapSort(int [] A){
        // 'n' representa o tamanho do heap, que inicialmente é o número de elementos a serem ordenados.
        int n = A.length - 1; // Ignora o índice 0, conforme o padrão comum em heapsorts de Cormen.
        
        // Fase 1: Construir um Max-Heap a partir do array de entrada.
        // Após esta chamada, A[1] será o maior elemento do array.
        buildMaxHeap(A, n);
        
        // Fase 2: Extrair elementos do heap um por um e construir o array ordenado.
        // O loop vai de i = n (último elemento do heap atual) até 2.
        for(int i = n; i >= 2; i--){
            // Troca o maior elemento (A[1], a raiz do heap) com o último elemento do heap (A[i]).
            // Agora, A[i] contém o maior elemento e está em sua posição final ordenada.
            swap(A, 1 , i); // Nota: 'i' é usado no lugar de 'n' na chamada original, aqui 'i' é o heapSize corrente
            
            // Reduz o tamanho efetivo do heap em 1, pois A[i] está agora ordenado e fora do heap.
            // O novo tamanho do heap para o maxHeapify é 'i-1'.
            // No seu código, você decrementa 'n' e usa esse 'n' atualizado.
            // Vamos seguir o código original: n--; maxHeapify(A,1,n)
            // Isso significa que o 'n' no loop for é na verdade o 'heapSize' que está diminuindo.
            // Na verdade, o 'n' original do heapsort deve ser 'i-1' para o maxHeapify.
            // A sua implementação original decrementa 'n' e passa para maxHeapify, que é correto.
            // A[1] agora tem o valor que estava em A[i], que pode não ser o maior.
            // É preciso restaurar a propriedade de Max-Heap na raiz A[1] para o heap de tamanho (i-1).
            maxHeapify(A, 1, i-1); // Corrige para o tamanho do heap restante
        }
    }

    // Retorna o maior elemento de um Max-Heap (a raiz).
    public static int heapMaximum(int[] A){
        // A[0] é ignorado, o heap começa em A[1].
        return A[1];
    }

    // Remove e retorna o maior elemento do Max-Heap.
    // 'heapSize' deve ser o tamanho atual do heap (quantos elementos válidos existem).
    // Este método modifica o array A e o heapSize deve ser gerenciado externamente.
    public static int heapExtractMax(int[] A, int currentHeapSize){ // Renomeado parâmetro para clareza
        if(currentHeapSize < 1){
            throw new RuntimeException("Heap underflow: Não possui elementos para remover.");
        }
        // O maior elemento está na raiz (A[1]).
        int max = A[1];

        // Move o último elemento do heap (A[currentHeapSize]) para a raiz.
        A[1] = A[currentHeapSize];

        // O tamanho do heap diminui em 1 (o elemento que estava em currentHeapSize efetivamente
        // foi movido ou será ignorado na próxima operação).
        // A responsabilidade de decrementar o heapSize real é de quem chama.
        // Aqui, para o maxHeapify, passamos o novo tamanho.
        maxHeapify(A, 1, currentHeapSize - 1);

        return max; // Retorna o maior elemento que foi extraído.
    }

    public static void main(String[] args){
        // O array de exemplo, com o índice 0 "ignorado" ou usado como sentinela.
        // Os elementos reais do heap/array a ser ordenado são de índice 1 a 7.
        int[] vetor = {0, 15, 7, 9, 20, 3, 1, 12}; // A[0] não usado pelo heapSort. n=7

        System.out.println("Vetor Original (índice 0 ignorado para heap): ");
        // Imprime a parte relevante do vetor
        System.out.println(Arrays.toString(Arrays.copyOfRange(vetor, 1, vetor.length)));

        HeapSort.heapSort(vetor); // Ordena o array 'vetor' in-place

        System.out.println("Vetor Ordenado (índice 0 pode conter lixo da ordenação): ");
        // Imprime a parte relevante do vetor, que agora está ordenada
        System.out.println(Arrays.toString(Arrays.copyOfRange(vetor, 1, vetor.length)));

        // Testando heapExtractMax
        System.out.println("\nTestando heapExtractMax:");
        int[] heapParaExtracao = {0, 25, 17, 19, 13, 10, 1, 5, 12, 11, 8}; // n=10
        int tamanhoHeapAtual = heapParaExtracao.length -1;
        System.out.println("Heap original: " + Arrays.toString(Arrays.copyOfRange(heapParaExtracao, 1, tamanhoHeapAtual + 1)));
        
        // Primeiro, construir o Max-Heap
        HeapSort.buildMaxHeap(heapParaExtracao, tamanhoHeapAtual);
        System.out.println("Após buildMaxHeap: " + Arrays.toString(Arrays.copyOfRange(heapParaExtracao, 1, tamanhoHeapAtual + 1)));
        System.out.println("Máximo (heapMaximum): " + HeapSort.heapMaximum(heapParaExtracao));
        
        int maxExtraido = HeapSort.heapExtractMax(heapParaExtracao, tamanhoHeapAtual);
        tamanhoHeapAtual--; // Atualiza o tamanho do heap após a extração
        System.out.println("Máximo extraído: " + maxExtraido);
        System.out.println("Heap após extração (tamanho " + tamanhoHeapAtual + "): " + Arrays.toString(Arrays.copyOfRange(heapParaExtracao, 1, tamanhoHeapAtual + 1)));

        maxExtraido = HeapSort.heapExtractMax(heapParaExtracao, tamanhoHeapAtual);
        tamanhoHeapAtual--;
        System.out.println("Máximo extraído: " + maxExtraido);
        System.out.println("Heap após extração (tamanho " + tamanhoHeapAtual + "): " + Arrays.toString(Arrays.copyOfRange(heapParaExtracao, 1, tamanhoHeapAtual + 1)));
    }
}

/*
Heap:
Um heap é uma estrutura de dados baseada em árvore binária que satisfaz a "propriedade do heap".
Existem dois tipos principais:
1.  **Max-Heap (Heap Máximo):** Para qualquer nó 'i' diferente da raiz, o valor do pai de 'i'
    é maior ou igual ao valor de 'i' (A[parent(i)] >= A[i]). Isso implica que o maior
    elemento do conjunto está sempre na raiz do heap.
2.  **Min-Heap (Heap Mínimo):** Para qualquer nó 'i' diferente da raiz, o valor do pai de 'i'
    é menor ou igual ao valor de 'i' (A[parent(i)] <= A[i]). O menor elemento está na raiz.

Um heap é também uma árvore binária "quase completa". Isso significa que todos os níveis da árvore
estão completamente preenchidos, exceto possivelmente o último nível, que é preenchido da
esquerda para a direita. Esta propriedade permite que um heap seja representado de forma compacta
usando um array.

Representação em Array (indexação a partir de 1, como no código):
- Se um nó está no índice 'i':
  - Seu pai está no índice `parent(i) = i / 2`.
  - Seu filho esquerdo está no índice `left(i) = 2 * i`.
  - Seu filho direito está no índice `right(i) = 2 * i + 1`.
- O array A[0] é frequentemente não utilizado ou usado para um propósito especial, com o heap
  realmente começando em A[1] e indo até A[n], onde 'n' é o tamanho do heap.

Operações Fundamentais do Heap (Max-Heap como exemplo):

1.  `MAX-HEAPIFY(A, i, n)`:
    - Objetivo: Restaurar a propriedade de Max-Heap em um nó 'i', assumindo que as subárvores
      esquerda e direita de 'i' (se existirem) já são Max-Heaps.
    - Como funciona: Compara A[i] com seus filhos A[left(i)] e A[right(i)]. Se A[i] não for o
      maior entre eles, ele é trocado com o maior dos filhos. Essa troca pode violar a
      propriedade de Max-Heap na subárvore do filho com o qual ocorreu a troca. Portanto,
      `MAX-HEAPIFY` é chamado recursivamente nessa subárvore.
    - Complexidade: $O(\log n)$, pois a "descida" no heap tem no máximo a altura da árvore,
      que é $\log n$.

2.  `BUILD-MAX-HEAP(A, n)`:
    - Objetivo: Construir um Max-Heap a partir de um array desordenado A[1...n].
    - Como funciona: Aplica `MAX-HEAPIFY` a todos os nós que não são folhas, em uma ordem
      de baixo para cima. Os nós folha já são Max-Heaps triviais. Os nós não-folha em um
      heap de 'n' elementos vão do índice $\lfloor n/2 \rfloor$ até 1.
    - Complexidade: Embora pareça $O(n \log n)$ (nós $n/2$ vezes $O(\log n)$), uma análise mais
      rigorosa mostra que é $O(n)$.

HeapSort:
O algoritmo HeapSort utiliza as operações de heap para ordenar um array.
1.  **Fase 1: Construção do Heap (`BUILD-MAX-HEAP(A, n)`)**
    - Transforma o array de entrada A[1...n] em um Max-Heap. Após esta fase, A[1] contém o
      maior elemento do array. Custo: $O(n)$.

2.  **Fase 2: Ordenação**
    - Repete $n-1$ vezes (de $i = n$ decrescendo até 2):
        a. Troca A[1] (o maior elemento do heap restante) com A[i]. Agora, A[i] está em sua
           posição final ordenada.
        b. O elemento que foi para A[1] (o antigo A[i]) pode violar a propriedade de Max-Heap.
           O tamanho efetivo do heap é reduzido para $i-1$.
        c. Chama `MAX-HEAPIFY(A, 1, i-1)` para restaurar a propriedade de Max-Heap na raiz
           do heap de tamanho reduzido.
    - Custo desta fase: $n-1$ chamadas a `MAX-HEAPIFY`, cada uma $O(\log k)$ onde $k$ é o
      tamanho atual do heap. Total: $O(n \log n)$.

Complexidade Total do HeapSort: $O(n) + O(n \log n) = O(n \log n)$.
Características do HeapSort:
- **In-place:** Realiza a ordenação modificando o array original, usando apenas uma
  quantidade constante de espaço adicional (para variáveis temporárias, não para estruturas
  de dados auxiliares que dependem do tamanho da entrada, se a recursão de `maxHeapify` for
  convertida para iterativa ou se a profundidade da recursão for considerada $O(\log n)$ na pilha).
- **Não estável:** A ordem relativa de elementos com chaves iguais pode não ser preservada.
- **Eficiência:** $O(n \log n)$ no pior caso, caso médio e melhor caso.

Outras Operações de Max-Heap (Relevantes para Filas de Prioridade):
- `HEAP-MAXIMUM(A)`: Retorna o elemento com a maior chave (A[1]). Custo: $O(1)$.
- `HEAP-EXTRACT-MAX(A, heapSize)`: Remove e retorna o elemento com a maior chave.
  - Salva A[1].
  - Move o último elemento do heap (A[heapSize]) para A[1].
  - Decrementa heapSize.
  - Chama `MAX-HEAPIFY(A, 1, heapSize)` para restaurar a propriedade.
  - Custo: $O(\log n)$.

Teste de Mesa (HeapSort para vetor = {0, 15, 7, 9, 20, 3, 1, 12}, n=7):
A = [?, 15, 7, 9, 20, 3, 1, 12]

1. buildMaxHeap(A, 7):
   - i = 7/2 = 3. maxHeapify(A,3,7) // A[3]=9. Filhos: A[6]=1, A[7]=12. Largest=7 (12). Swap(A[3],A[7]). A=[?,15,7,12,20,3,1,9]. maxHeapify(A,7,7) (ok).
   - i = 2. maxHeapify(A,2,7) // A[2]=7. Filhos: A[4]=20, A[5]=3. Largest=4 (20). Swap(A[2],A[4]). A=[?,15,20,12,7,3,1,9]. maxHeapify(A,4,7) (ok).
   - i = 1. maxHeapify(A,1,7) // A[1]=15. Filhos: A[2]=20, A[3]=12. Largest=2 (20). Swap(A[1],A[2]). A=[?,20,15,12,7,3,1,9]. maxHeapify(A,2,7).
     - maxHeapify(A,2,7) para A[2]=15. Filhos: A[4]=7, A[5]=3. Largest=2 (15). OK.
   Heap construído: A = [?, 20, 15, 12, 7, 3, 1, 9] (O nó 9 na pos 7 não tem filhos para o pai 12)
   Corrigindo o traço de buildMaxHeap(A,7) com A=[0, 15, 7, 9, 20, 3, 1, 12]:
    n=7.
    i=3 (A[3]=9). l=6(A[6]=1), r=7(A[7]=12). largest=7. swap(A[3],A[7]). A=[0,15,7,12,20,3,1,9]. maxHeapify(A,7,7) -> ok.
    i=2 (A[2]=7). l=4(A[4]=20), r=5(A[5]=3). largest=4. swap(A[2],A[4]). A=[0,15,20,12,7,3,1,9]. maxHeapify(A,4,7) -> ok.
    i=1 (A[1]=15). l=2(A[2]=20), r=3(A[3]=12). largest=2. swap(A[1],A[2]). A=[0,20,15,12,7,3,1,9]. maxHeapify(A,2,7).
        maxHeapify(A,2,7) com A[2]=15. l=4(A[4]=7), r=5(A[5]=3). largest=2. ok.
    Final buildMaxHeap: A = [0, 20, 15, 12, 7, 3, 1, 9]

2. Loop do heapSort:
   heapSize atual = 7.
   i = 7: swap(A[1],A[7]) -> A=[0,9,15,12,7,3,1,20]. maxHeapify(A,1,6).
     A[1]=9. l=2(15), r=3(12). largest=2. swap(A[1],A[2]). A=[0,15,9,12,7,3,1,20]. maxHeapify(A,2,6).
       A[2]=9. l=4(7), r=5(3). largest=2. ok.
     Heap (tam 6): [0,15,9,12,7,3,1]. Ordenado (pos 7): [20].

   heapSize atual = 6.
   i = 6: swap(A[1],A[6]) -> A=[0,1,9,12,7,3,15,20]. maxHeapify(A,1,5).
     A[1]=1. l=2(9), r=3(12). largest=3. swap(A[1],A[3]). A=[0,12,9,1,7,3,15,20]. maxHeapify(A,3,5).
       A[3]=1. l=N/A (6>5). largest=3. ok.
     Heap (tam 5): [0,12,9,1,7,3]. Ordenado (pos 6,7): [15,20].

   heapSize atual = 5.
   i = 5: swap(A[1],A[5]) -> A=[0,3,9,1,7,12,15,20]. maxHeapify(A,1,4).
     A[1]=3. l=2(9), r=3(1). largest=2. swap(A[1],A[2]). A=[0,9,3,1,7,12,15,20]. maxHeapify(A,2,4).
       A[2]=3. l=4(7). r=N/A. largest=4. swap(A[2],A[4]). A=[0,9,7,1,3,12,15,20]. maxHeapify(A,4,4) -> ok.
     Heap (tam 4): [0,9,7,1,3]. Ordenado (pos 5,6,7): [12,15,20].

   heapSize atual = 4.
   i = 4: swap(A[1],A[4]) -> A=[0,3,7,1,9,12,15,20]. maxHeapify(A,1,3).
     A[1]=3. l=2(7), r=3(1). largest=2. swap(A[1],A[2]). A=[0,7,3,1,9,12,15,20]. maxHeapify(A,2,3) -> ok.
     Heap (tam 3): [0,7,3,1]. Ordenado (pos 4..7): [9,12,15,20].

   heapSize atual = 3.
   i = 3: swap(A[1],A[3]) -> A=[0,1,3,7,9,12,15,20]. maxHeapify(A,1,2).
     A[1]=1. l=2(3). r=N/A. largest=2. swap(A[1],A[2]). A=[0,3,1,7,9,12,15,20]. maxHeapify(A,2,2) -> ok.
     Heap (tam 2): [0,3,1]. Ordenado (pos 3..7): [7,9,12,15,20].

   heapSize atual = 2.
   i = 2: swap(A[1],A[2]) -> A=[0,1,3,7,9,12,15,20]. maxHeapify(A,1,1) -> ok.
     Heap (tam 1): [0,1]. Ordenado (pos 2..7): [3,7,9,12,15,20].

   Loop termina. Array (parte relevante A[1..7]) ordenado: [1, 3, 7, 9, 12, 15, 20].
*/