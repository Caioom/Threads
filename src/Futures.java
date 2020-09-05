import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Futures {
    private static final ExecutorService executor = Executors.newFixedThreadPool(3);

    public static List<Future<String>> executarTarefas(Casa casa) {
       return casa.obterTarefas()
                .stream()
                .map(tarefa -> executor.submit(() -> {
                    try {
                        return tarefa.realizar();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return null;
                })).collect(Collectors.toList());
    }

    public static boolean verificarSeExiste(List<Future<String>> futuresEmExecucao) {
        if(futuresEmExecucao.size() == 0)
            return false;

        return true;
    }

    public static void exibirFeito(Future future) throws ExecutionException, InterruptedException {
        System.out.println("Parabens por " + future.get());
        System.out.println("________________________________");
    }


    public static List<Future<String>> removerRealizadas(List<Future<String>> futures) throws ExecutionException, InterruptedException {
        List<Future<String>> futuresEmExecucao = futures;
        while(true) {
            if(!verificarSeExiste(futuresEmExecucao)) {
                break;
            }

            for(Future future : futures) {
                if(future.isDone()) {
                    exibirFeito(future);
                    futures.remove(future);
                }
            }
        }

        return futuresEmExecucao;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Casa casa = new Casa(new Quarto());
        List<Future<String>> futures = new CopyOnWriteArrayList<>(executarTarefas(casa));

        if(removerRealizadas(futures).size() == 0) {
            System.out.println("\nPARABENS VOCE ACABOU TODAS AS TAREFAS");
            executor.shutdown();
        };
    }
}

class Casa {
    private List<Comodo> comodos;

    public Casa(Comodo... comodos) {
        this.comodos = Arrays.asList(comodos);
    }

    public List<Tarefa> obterTarefas() {
        return this.comodos
                .stream()
                .map(Comodo::retornarTarefas)
                .reduce(new ArrayList<>(), (pivo, tarefas) -> {
                    pivo.addAll(tarefas);
                    return pivo;
                });
    }
}

@FunctionalInterface
interface Tarefa {
    String realizar() throws InterruptedException;
}

abstract class Comodo {
    abstract List<Tarefa> retornarTarefas();
}

class Quarto extends Comodo {
    @Override
    List<Tarefa> retornarTarefas() {
        return Arrays.asList(
                this::arrumarCama,
                this::arrumarGaveta,
                this::arrumarMesa,
                this::arrumarGuardaRoupa
        );
    }

    public String arrumarCama() throws InterruptedException {
        Thread.sleep(5000);
        return "Arrumar cama";
    }

    public String arrumarGaveta() {
        return"Arrumar Gaveta";
    }

    public String arrumarMesa() {
        return "Arrumar Mesa";
    }

    public String arrumarGuardaRoupa() {
        return "Arrumar Guarda Roupa";
    }
}
