import java.util.Comparator;

public class PrioritySorter implements Comparator<HandleAClient>
{
    @Override
    public int compare(HandleAClient o1, HandleAClient o2) {
        return o1.str.getPriority() - o2.str.getPriority();
    }
}
