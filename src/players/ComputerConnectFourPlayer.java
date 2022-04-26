package players;

import java.lang.Math;
import java.util.ArrayList;
//import Move;

public class ComputerConnectFourPlayer implements ConnectFourPlayer {

	private int side; //comupter side
	private int otherSide;
	private int maxDepth;

	/**
	* Constructor for the computer player.
	* @param depth the number of plies to look ahead
	* @param side -1 or 1, depending on which player this is
	*/
	public ComputerConnectFourPlayer(int depth, byte side) {
		this.side = side;
		otherSide = (-1)*side;
		maxDepth = depth;
	}

	/**
	* This computer is smart. It plays the best move returned by the miniMax algorithm.
	* @param rack the current rack
	* @return the column to play
	*/
	public int getNextPlay(byte[][] rack) {
		return miniMax(rack);
	}

	/**
	* This prints the rack for debugging purposes
	* @param rack the current rack
	*/
	private void printRack(byte[][] rack){
		System.out.println("\n");
		int height = rack.length;
		int width = rack[0].length;

		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				System.out.print(rack[i][j]);
			}
			System.out.println("");
		}
	}

	/**
	* Assesses a rack using the miniMax algorithm with the specified depth cutoffTest
	* and returns the optimal move to make.
	* @param rack the current rack
	* @return the optimal move to make (integer of the column to play)
	*/
	private int miniMax(byte[][] rack){
		Move bestMove = maxVal(rack, 0, maxDepth);
		return bestMove.getAction();
	}

	/**
	*	Calcualtes the move with the highest possible value for the player, taking
	* into account the
	* @param rack the current rack
	* @param prevAction the last action taken (int # of column played)
	* @param depth the current depth of the search
	* @return the best move to take looking ahead the indicated depth
	*/
	private Move maxVal(byte[][] rack, int previousAction, int depth){
		//if the rack has a winner or has hit the max depth, return the current utility
		if (terminalTest(rack) || depth > maxDepth){
			Move m = new Move(evaluate(rack), previousAction);
			return m;
		}
		int value = Integer.MIN_VALUE;
		int action = 0;
		//collect all possible actions user could make.
		int actions[] = actions(rack);

		for (int i = 0; i < actions.length; i++){
			//Recursively compute the highest possible utility (for MAX) if MAX were to take this action
			int u = minVal(result(rack, actions[i], side), actions[i], depth).getValue();
			if (u > value){
				value = u;
				action = actions[i];
			}
		}

		//return the optimal move
		Move move = new Move(value, action);
		return move;
	}

	/**
	* Calcualtes the move with the lowest possible value for the player's opponent,
	* which from their perspective is their hightest possible value.
	* @param rack the current rack
	* @param prevAction the last action taken (int # of column played)
	* @param depth the current depth of the search
	* @return the best move to take looking ahead the indicated depth
	*/
	private Move minVal(byte[][] rack, int previousAction, int depth){
		//if the rack has a winner or has hit the max depth, return the current utility
		if (terminalTest(rack) || depth > maxDepth){
			Move m = new Move(evaluate(rack), previousAction);
			return m;
		}
		int value = Integer.MAX_VALUE;
		int action = 0;
		//collect all possible actions user could make.
		int actions[] = actions(rack);
		//update the depth every 2 ply
		int thisdepth = depth + 1;

		for (int i = 0; i < actions.length; i++){
			// Compute the lowest possible utility (for MAX) if MIN were to take this action
			int u = maxVal(result(rack, actions[i], otherSide), actions[i], thisdepth).getValue();
			if (u < value){
				value = u;
				action = actions[i];
			}
		}

		//return the optimal move
		Move move = new Move(value, action);
		return move;
	}

	/**
	* Checks to see if someone won or if all of the board positions are full.
	* @param rack the current rack
	* @return true if rack contains a win or if rack is full, otherwise false.
	*/
	private boolean terminalTest(byte[][] rack){
		//game over if someone won
		if(Math.abs(evaluate(rack)) > 100000){
			return true;
		}

		//game over if board is full
		int height = rack.length;
		int width = rack[0].length;
		int count = 0;
		for(int h = 0; h < height; h++){
			for(int w = 0; w < width; w++){
				if(rack[h][w] != 0){
					count++;
				}
			}
		}
		if(count == (width * height)){
			return true;
		}

		//otherwise game is not over
		return false;

	}

	/**
	*	A heuristic that approximates how good a rack is for the player. The larger
	* the heruistic result, the better the board.
	* @param rack the current rack
	* @return an integer heuristic of the rack
	*/
	private int evaluate(byte[][] rack){

		//get rack size
		int height = rack.length;
		int width = rack[0].length;

		boolean won = false;
		int score = 0; //the score to be returned

		//horizontal
		int hAI;
		int hOpp;
		for(int h = 0; h < height; h++){ //in each row
			for(int w = 0; w <= (width/2); w++){ //4 possible horizontal wins
				hAI = 0;
				hOpp = 0;
				for(int i = 0; i < 4; i ++){ //check 4 positions
					if(rack[h][w+i] == side){
						hAI++;
					}
					else if(rack[h][w+i] == otherSide){
						hOpp++;
					}
				}

				if(hAI == 4){
					//All four tokens were AI... AI wins
					score = Integer.MAX_VALUE;
					won = true;
					h = height+1;
					w = width+1;
					break;
				}
				else if(hAI == 3 && hOpp == 0){
					//If it has 3 tokens of the AI's color and none of the opponent's, it is worth 100 points.
					score += 100;
				}
				else if(hAI == 2 && hOpp == 0){
					//If it has 2 tokens of the AI's color and none of the opponent's, it is worth 10 points.
					score += 10;
				}
				else if(hAI == 1 && hOpp == 0){
					//If it has 1 token of the AI's color and none of the opponent's, it is worth 1 points.
					score += 1;
				}
				else if(hOpp == 4){
					//All four tokens were opponent's... opponent wins
					score = Integer.MIN_VALUE;
					won = true;
					h = height+1;
					w = width+1;
					break;
				}
				else if(hOpp == 3 && hAI == 0){
					//3 of the opponent's tokens but none of the AI's should be worth -100 points.
					score -= 100;
				}
				else if(hOpp == 2 && hAI == 0){
					//2 of the opponent's tokens but none of the AI's should be worth -10 points.
					score -= 10;
				}
				else if(hOpp == 1 && hAI == 0){
					//2 of the opponent's tokens but none of the AI's should be worth -1 points.
					score -= 1;
				}
				//If it has no tokens, or it has a mix of the AI's and the opponent's tokens, it is worth 0 points.
			}
		}

		//vertical
		if(won == false){
			int vAI;
			int vOpp;
			for(int w = 0; w < width; w++){
				for(int h = 0; h < (height/2); h ++){
					vAI = 0;
					vOpp = 0;
					for(int i = 0; i < 4; i++){
						if(rack[h+i][w] == side){
							vAI++;
						}
						else if(rack[h+i][w] == otherSide){
							vOpp++;
						}
					}
					if(vAI == 4){
						//All four tokens were AI... AI wins
						score = Integer.MAX_VALUE;
						won = true;
						h = height+1;
						w = width+1;
						break;
					}
					else if(vAI == 3 && vOpp == 0){
						//If it has 3 tokens of the AI's color and none of the opponent's, it is worth 100 points.
						score += 100;
					}
					else if(vAI == 2 && vOpp == 0){
						//If it has 2 tokens of the AI's color and none of the opponent's, it is worth 10 points.
						score += 10;
					}
					else if(vAI == 1 && vOpp == 0){
						//If it has 1 token of the AI's color and none of the opponent's, it is worth 1 points.
						score += 1;
					}
					else if(vOpp == 4){
						//All four tokens were opponent's... opponent wins
						score = Integer.MIN_VALUE;
						won = true;
						h = height+1;
						w = width+1;
						break;
					}
					else if(vOpp == 3 && vAI == 0){
						//3 of the opponent's tokens but none of the AI's should be worth -100 points.
						score -= 100;
					}
					else if(vOpp == 2 && vAI == 0){
						//2 of the opponent's tokens but none of the AI's should be worth -10 points.
						score -= 10;
					}
					else if(vOpp == 1 && vAI == 0){
						//2 of the opponent's tokens but none of the AI's should be worth -1 points.
						score -= 1;
					}
					//If it has no tokens, or it has a mix of the AI's and the opponent's tokens, it is worth 0 points.
				}
			}
		}

		//descending
		if(won == false){
			int dAI;
			int dOpp;
			for(int h = 0; h <= (height/2); h++){
				for(int w = 0; w <= (width/2); w++){
					dAI = 0;
					dOpp = 0;
					for(int i = 0; i < 3; i++){
						if(rack[h+i][w+i] == side){
							dAI++;
						}
						else if(rack[h+i][w+i] == otherSide){
							dOpp++;
						}
					}
					if(dAI == 4){
						//All four tokens were AI... AI wins
						score = Integer.MAX_VALUE;
						won = true;
						h = height+1;
						w = width+1;
						break;
					}
					else if(dAI == 3 && dOpp == 0){
						//If it has 3 tokens of the AI's color and none of the opponent's, it is worth 100 points.
						score += 100;
					}
					else if(dAI == 2 && dOpp == 0){
						//If it has 2 tokens of the AI's color and none of the opponent's, it is worth 10 points.
						score += 10;
					}
					else if(dAI == 1 && dOpp == 0){
						//If it has 1 token of the AI's color and none of the opponent's, it is worth 1 points.
						score += 1;
					}
					else if(dOpp == 4){
						//All four tokens were opponent's... opponent wins
						score = Integer.MIN_VALUE;
						won = true;
						h = height+1;
						w = width+1;
						break;
					}
					else if(dOpp == 3 && dAI == 0){
						//3 of the opponent's tokens but none of the AI's should be worth -100 points.
						score -= 100;
					}
					else if(dOpp == 2 && dAI == 0){
						//2 of the opponent's tokens but none of the AI's should be worth -10 points.
						score -= 10;
					}
					else if(dOpp == 1 && dAI == 0){
						//2 of the opponent's tokens but none of the AI's should be worth -1 points.
						score -= 1;
					}
					//If it has no tokens, or it has a mix of the AI's and the opponent's tokens, it is worth 0 points.
				}
			}
		}

		//ascending
		if(won == false){
			int aAI;
			int aOpp;
			for(int h = (height/2); h < height; h++){
				for(int w = 0; w <= (width/2); w++){
					aAI = 0;
					aOpp = 0;
					for (int i = 0; i <= 3; i++){
						if(rack[h-i][w+i] == side){
							aAI++;
						}
						else if(rack[h-i][w+i] == otherSide){
							aOpp++;
						}
					}
					if(aAI == 4){
						//All four tokens were AI... AI wins
						score = Integer.MAX_VALUE;
						won = true;
						h = height+1;
						w = width+1;
						break;
					}
					else if(aAI == 3 && aOpp == 0){
						//If it has 3 tokens of the AI's color and none of the opponent's, it is worth 100 points.
						score += 100;
					}
					else if(aAI == 2 && aOpp == 0){
						//If it has 2 tokens of the AI's color and none of the opponent's, it is worth 10 points.
						score += 10;
					}
					else if(aAI == 1 && aOpp == 0){
						//If it has 1 token of the AI's color and none of the opponent's, it is worth 1 points.
						score += 1;
					}
					else if(aOpp == 4){
						//All four tokens were opponent's... opponent wins
						score = Integer.MIN_VALUE;
						won = true;
						h = height+1;
						w = width+1;
						break;
					}
					else if(aOpp == 3 && aAI == 0){
						//3 of the opponent's tokens but none of the AI's should be worth -100 points.
						score -= 100;
					}
					else if(aOpp == 2 && aAI == 0){
						//2 of the opponent's tokens but none of the AI's should be worth -10 points.
						score -= 10;
					}
					else if(aOpp == 1 && aAI == 0){
						//2 of the opponent's tokens but none of the AI's should be worth -1 points.
						score -= 1;
					}
					//If it has no tokens, or it has a mix of the AI's and the opponent's tokens, it is worth 0 points.
				}
			}
		}

		//the final accumulated heuristic score of the rack.
		return score;
	}

	/**
	* Finds all of the possible actions the player can take.
	* @param rack the current rack
	* @return array containing the column numbers for all possible actions
	*/
	private int[] actions(byte[][] rack){
		//get rack dimensions
		int height = rack.length;
		int width = rack[0].length;

		ArrayList<Integer> actions = new ArrayList<Integer>();

		int numTokens;
		//count the number of tokens in each column
		for(int w = 0; w < width; w++){
			numTokens = 0;
			for(int h = 0; h < height; h++){
				if(rack[h][w] != 0){
					numTokens++;
				}
			}
			//if there's room in the column, add it to possible actions
			if(numTokens < height){
				actions.add(w);
			}
		}

		//Copy possible actions to array and return
		int[] retActions = new int[actions.size()];
		for (int i=0; i < retActions.length; i++)
		{
			retActions[i] = actions.get(i).intValue();
		}

		//return array of possible legal actions
		return retActions;
	}

	/**
	* Given a rack and an action, updates the rack to perform the action.
	* @param rack the current rack
	* @param action an action to be taken
	* @param thisSide the side taking the action
	* @return a rack with the input action taken
	*/
	private byte[][] result(byte[][] rack, int action, int thisSide){
		int height = rack.length;
		int width = rack[0].length;

		byte[][] newRack = new byte[height][width];
		for(int i = 0; i < height; i++){
			newRack[i] = rack[i].clone();
		}

		int w = action;
		for(int h = height-1; h >= 0; h--){
			//find where the new token will rest in the colomn and update the rack
			if(newRack[h][w] == 0){
				newRack[h][w] = (byte) thisSide;
				h = -1;
				break;
			}
		}

		return newRack;
	}

	/**
	* A Move object holds a value and an action.
	*/
	public class Move{
		private int value;
		private int action;

		/**
		* Constructs a Move with the input value and action.
		* @param value a heuristic evaluation value
		* @param action an action (column #)
		*/
		public Move(int value, int action){
			this.value = value;
			this.action = action;
		}

		/**
		* Returns the Move's value
		* @return the value of the Move
		*/
		public int getValue(){
			return value;
		}

		/**
		* Returns the Move's action
		* @return the action of the Move
		*/
		public int getAction(){
			return action;
		}
	}
}
