package players;

public class Move{
  private int value;
  private int action;

  public Move(int value, int action){
    this.value = value;
    this.action = action;
  }

  public int getValue(){
    return value;
  }

  public int getAction(){
    return action;
  }

}
