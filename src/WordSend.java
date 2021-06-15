import java.io.Serializable;

public class WordSend implements Serializable {
    String text;
    int priority;
    int time = 0;

    public WordSend(String text, int priority) {
        this.text = text;
        this.priority = priority;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getTime()
    {
        return time;
    }

    public void hadToWait()
    {
        time+=1;
    }

}
