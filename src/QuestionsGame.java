import java.io.*;
import java.util.*;

/**
 *
 * The primary class of the program: QuestionsGame
 * This class handles all functionality related to the tree of questions.
 *
 */

public class QuestionsGame {

    private QuestionNode root;

    /**
     * This constructor is used to add a new object for the computer to guess when the player creates a new question document
     * @param object is a string which contains the name of an object that the program can guess
     */
    public QuestionsGame(String object){
        root = new QuestionNode(object);
    }

    /**
     * This constructor is used to initialize a question tree from a document of questions
     * See: createTree
     * @param input
     */
    public QuestionsGame(Scanner input){
        root = new QuestionNode(createTree(input));
    }

    /**
     * This helper method recursively adds new nodes to a tree by reading data from a questions text document
     * @param input is the scanner attached to the document being read
     * @return a new node, which, once recursion is complete, will be the root node
     */
    private QuestionNode createTree(Scanner input){
        String type = input.nextLine();
        String value = input.nextLine();
        if(type.contains("Q"))
            return new QuestionNode(value, createTree(input), createTree(input));
        else
            return new QuestionNode(value);
    }

    /**
     * This method saves the new question tree to the questions document being used
     * See: saveHelper
     * @param output is the print stream attached to the questions document
     */
    public void saveQuestions(PrintStream output){
        if(output == null)
            throw new IllegalArgumentException("PrintStream is null.");

        saveHelper(output, root);
    }

    /**
     * This helper method iterates through the tree using pre-order traversal
     * It writes the data of each node to the questions document with proper formatting
     * @param output is the print stream attached to the questions document
     * @param node is the current node the method is reading
     */
    private void saveHelper(PrintStream output, QuestionNode node){
        if(node != null){
            output.println(node.toString());
            saveHelper(output, node.left);
            saveHelper(output, node.right);
        }
    }

    /**
     * This method handles the mechanics of the game:
     *      text output, user input, win detection, adding new questions, and adding new objects
     * See: playHelper, addQuestion
     * @param input is the scanner attached to the user's input (typically the console)
     */
    public void play(Scanner input){
        playHelper(input, root, null);
    }

    /**
     * This helper method handles the navigation through the question tree as the game progresses
     * It receives and checks user input after each prompt
     * If the computer fails, it adds a new question and new object provided by the user
     * @param input is the scanner attached to the user's input
     * @param node is the current node the method is reading
     * @param parent is the previous node that was read
     *               (this is used to properly rearrange nodes when new items are added)
     */
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

    /**
     * This helper method handles all actions involving adding new questions and objects
     * @param parentParent is the parent of the new question that will be added
     * @param newParent is a string containing the new question that will be added
     * @param newChild is the node that will be shifted down into a child of the new question node
     * @param newValue is a string containing the name of the new object that will be added
     * @param answerIsYes is a boolean value indicating if responding to the new question with 'yes'
     *                    will point to newValue or newChild
     *                    (true if yes points to newValue, false if yes points to newChild)
     */
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

    /**
     *
     * The QuestionNode class is the core component of the question tree that is generated
     * It has:
     *      a string containing its value
     *      a node to its left
     *      a node to its right
     *      a boolean value indicating if it is a question (false if it is an answer)
     *
     *
     */
    private static class QuestionNode {
        public final String value;
        public QuestionNode left;
        public QuestionNode right;
        public final boolean isQuestion;

        /**
         * This constructor initializes a new node by copying the values of a different node to it
         * It is used to add new questions and objects to the question tree
         * @param other is the node which the data will be read from
         */
        public QuestionNode(QuestionNode other){
            this.value = other.value;
            this.left = other.left;
            this.right = other.right;
            this.isQuestion = other.isQuestion;
        }

        /**
         * This constructor initializes a new node from only a string value
         * It is exclusively used to create new object nodes, thus isQuestion is always set to false
         * @param value is a string containing the object the node will represent
         */
        public QuestionNode(String value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.isQuestion = false;
        }

        /**
         * This constructor initializes a new node from a string value, a left node, and a right node
         * It is exclusively used to create new question nodes, thus isQuestion is always set to true
         * @param value is a string containing the question the node will represent
         * @param left is a node that is to the left of this node
         *             (the next question/answer if the response to this question is yes)
         * @param right is a node that is to the right of this node
         *              (the next question/answer if the response to this question is no)
         */
        public QuestionNode(String value, QuestionNode left, QuestionNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
            this.isQuestion = true;
        }

        /**
         * A setter function for the left node
         * @param left is a node that is to the left of this node
         *             (the next question/answer if the response to this question is yes)
         */
        public void setLeft(QuestionNode left) {
            this.left = left;
        }

        /**
         * A setter function for the right node
         * @param right is a node that is to the right of this node
         *              (the next question/answer if the response to this question is no)
         */
        public void setRight(QuestionNode right) {
            this.right = right;
        }

        /**
         * Overrides the toString method for QuestionNode
         * @return a string value that is formatted properly for the question document
         *         (if the node is a question node, preface it with 'Q:' and a newline
         *         if the node is an answer node, preface it with 'A:' and a newline)
         */
        @Override
        public String toString(){
            if(isQuestion)
                return "Q:\n" + value;
            else
                return "A:\n" + value;
        }
    }
}
