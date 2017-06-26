import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Administrator on 2017/5/18.
 */
public class Demo {
    public static void main(String[] args) {
        try {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                //list.add(i);
            }
            System.out.println(list.toString());
            System.out.println(new Demo().list2Str(list, 5));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String list2Str(List<String> list, final int nThreads) throws Exception {
        if (list == null || list.isEmpty()) {
            return null;
        }

        StringBuffer ret = new StringBuffer();

        int size = list.size();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        List<Future<List>> futures = new ArrayList<>(nThreads);

        for (int i = 0; i < nThreads; i++) {
            final List<String> subList = list.subList(size / nThreads * i, size / nThreads * (i + 1));
            Callable<List> task = new Callable<List>() {
                @Override
                public List call() throws Exception {
                    System.out.println(Thread.currentThread().getName()+"执行！"+System.currentTimeMillis());
                    return subList;
                }
            };
            futures.add(executorService.submit(task));
        }

        for (Future<List> future : futures) {
            System.out.println(future.get().toString());
            ret.append(future.get());
        }
        executorService.shutdown();

        return ret.toString();
    }
}
