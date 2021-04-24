import java.io.*;
import java.util.*;

public class QuestionsGame {

    private QuestionNode root;

    public QuestionsGame(String object){
        root = new QuestionNode(object);
    }

    public QuestionsGame(Scanner input){
        root = new QuestionNode(createTree(input));
    }

    private QuestionNode createTree(Scanner input){
        String type = input.nextLine();
        String value = input.nextLine();
        if(type.contains("Q"))
            return new QuestionNode(value, createTree(input), createTree(input));
        else
            return new QuestionNode(value);
    }

    public void saveQuestions(PrintStream output){
        if(output == null)
            throw new IllegalArgumentException("PrintStream is null.");

        saveHelper(output, root);
    }

    private void saveHelper(PrintStream output, QuestionNode node){
        if(node != null){
            output.println(node.toString());
            saveHelper(output, node.left);
            saveHelper(output, node.right);
        }
    }

    public void play(Scanner input){
        playHelper(input, root, null);
    }

    private void playHelper(Scanner input, QuestionNode node, QuestionNode parent){
        if(node != null){
            if(node.isQuestion) {
                System.out.print(node.value + " (y/n)? ");
                String answer = input.nextLine();
                if (answer.trim().toLowerCase().startsWith("y"))
                    playHelper(input, node.left, node);
                else
                    playHelper(input, node.right, node);
            } else {
                System.out.println("I guess that your object is " + node.value + "!");
                System.out.print("Am I right? (y/n)? ");
                String answer = input.nextLine();
                if(answer.trim().toLowerCase().startsWith("y")){
                    System.out.println("Awesome! I win!");
                } else {
                    System.out.println("Boo! I Lose.  Please help me get better!");
                    System.out.print("What is your object? ");
                    String object = input.nextLine().trim();
                    System.out.println("Please give me a yes/no question that distinguishes between " + object + " and " + node.value + ".");
                    System.out.print("Q: ");
                    String question = input.nextLine().trim();
                    System.out.print("Is the answer \"yes\" for " + object + "? (y/n)? ");
                    answer = input.nextLine();

                    addQuestion(parent, question, node, object, answer.trim().toLowerCase().startsWith("y"));
                }
            }
        } else {
            System.out.println("There was some error.");
        }


    }

    private void addQuestion(QuestionNode parentParent, String newParent, QuestionNode newChild, String newValue, boolean answerIsYes){
        if(parentParent != null) {
            if (answerIsYes) {
                if (parentParent.left == newChild)
                    parentParent.setLeft(new QuestionNode(newParent, new QuestionNode(newValue), newChild));
                else if (parentParent.right == newChild)
                    parentParent.setRight(new QuestionNode(newParent, new QuestionNode(newValue), newChild));
                else
                    System.out.println("There was an error with the tree.");
            } else {
                if (parentParent.left == newChild)
                    parentParent.setLeft(new QuestionNode(newParent, newChild, new QuestionNode(newValue)));
                else if (parentParent.right == newChild)
                    parentParent.setRight(new QuestionNode(newParent, newChild, new QuestionNode(newValue)));
                else
                    System.out.println("There was an error with the tree.");
            }
        } else {
            if (answerIsYes)
                root = new QuestionNode(newParent, new QuestionNode(newValue), newChild);
            else
                root = new QuestionNode(newParent, newChild, new QuestionNode(newValue));
        }
    }

    private static class QuestionNode {
        public final String value;
        public QuestionNode left;
        public QuestionNode right;
        public final boolean isQuestion;

        public QuestionNode(QuestionNode other){
            this.value = other.value;
            this.left = other.left;
            this.right = other.right;
            this.isQuestion = other.isQuestion;
        }

        public QuestionNode(String value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.isQuestion = false;
        }

        public QuestionNode(String value, QuestionNode left, QuestionNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
            this.isQuestion = true;
        }

        public void setLeft(QuestionNode left) {
            this.left = left;
        }

        public void setRight(QuestionNode right) {
            this.right = right;
        }

        @Override
        public String toString(){
            if(isQuestion)
                return "Q:\n" + value;
            else
                return "A:\n" + value;
        }
    }
}
