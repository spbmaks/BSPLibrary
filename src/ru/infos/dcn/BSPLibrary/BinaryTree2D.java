package ru.infos.dcn.BSPLibrary;

import javax.swing.*;
import java.util.Arrays;

import static java.lang.Math.*;
import static java.lang.System.arraycopy;
import static java.lang.System.out;

/**
 * Created by IntelliJ IDEA.
 * User: mike
 * Date: 14.03.12
 * Time: 20:01
 * To change this template use File | Settings | File Templates.
 */
public final class BinaryTree2D {
    //инициализация компораторов
    PointsComparatorX pointsComparatorX = new PointsComparatorX();
    PointsComparatorY pointsComparatorY = new PointsComparatorY();
    final Node rootNode = new Node();
    Point[] sourcePoints;
    static class Node {
        Node left;
        Node right;
        Point[] value;
        int sortType;
        int edgeCoordMin;
        int edgeCoordMax;

        public Node(Point[] value) {
            this.value = value;
        }

        public Node() {
        }
    }

    public BinaryTree2D (Point[] points){
        sourcePoints = points;  //потом сделать не копию по ссылке а копированием значений?
        this.makeBinaryTree2D(rootNode, points);
    }

    private void makeBinaryTree2D(Node rootNode, Point[] pointsArray){
        recursiveTreeBuilding(rootNode, pointsArray, 0);
    }

    private void recursiveTreeBuilding(Node rootNode, Point[] pointsArray, int sortType) {
        sortType = sortType % 2;
        if (sortType == 0){
            Arrays.sort(pointsArray, pointsComparatorX);
        }
        else if (sortType == 1){
            Arrays.sort(pointsArray, pointsComparatorY);
        }
        int leftNodeSize = (int)floor(pointsArray.length / 2);
        int rightNodeSize = pointsArray.length-leftNodeSize;
        Point[] nodePoints1;
        Point[] nodePoints2;

        nodePoints1 = new Point[leftNodeSize];
        arraycopy(pointsArray, 0, nodePoints1, 0, leftNodeSize);
        if (nodePoints1.length > Stucture.minLeafSize * 2) {
            insert(rootNode, null, true, sortType, nodePoints1[nodePoints1.length - 1].coord[sortType]);
            recursiveTreeBuilding(rootNode.left, nodePoints1, sortType + 1);
        }
        else{
            insert(rootNode, nodePoints1, true, sortType, nodePoints1[nodePoints1.length - 1].coord[sortType]);
        }
        nodePoints2 = new Point[rightNodeSize];
        arraycopy(pointsArray, leftNodeSize, nodePoints2, 0, rightNodeSize);
        if (nodePoints2.length > Stucture.minLeafSize * 2) {
            insert(rootNode, null, false, sortType, pointsArray[leftNodeSize].coord[sortType]);
            recursiveTreeBuilding(rootNode.right, nodePoints2, sortType + 1);
        }
        else{
            insert(rootNode, nodePoints2, false, sortType, pointsArray[leftNodeSize].coord[sortType]);
        }
        rootNode.value = null;
    }

    private void insert(Node node, Point[] value, boolean isLeft, int sortType, int edgeCoord) {
        if (isLeft) {
            if (node.left != null) {
                insert(node.left, value, isLeft, sortType, edgeCoord);
            } else {
                node.left = new Node(value);
                node.sortType = sortType;
                node.edgeCoordMin = edgeCoord;
            }
        } else {
            if (node.right != null) {
                insert(node.right, value, isLeft, sortType, edgeCoord);
            } else {
                node.right = new Node(value);
                node.sortType = sortType;
                node.edgeCoordMax = edgeCoord;
            }
        }
    }

//    private void treeTraverse(Node node){
//        if (node != null) {
//            for (Point p : node.value){
//                pointsInLeafs.add(p);
//                treeTraverse(node.left);
//                treeTraverse(node.right);
//            }
//        }
//    }

    /* Function to print level order traversal a tree*/
    int height(Node node)
    {
        if (node==null)
            return 0;
        else
        {
            /* compute the height of each subtree */
            int lheight = height(node.left);
            int rheight = height(node.right);

            /* use the larger one */
            if (lheight > rheight)
                return(lheight+1);
            else return(rheight+1);
        }
    }

    void printLevelOrder(Node root)
    {
        int h = height(root);
        int i;
        for(i=1; i<=h; i++){
            out.println("\nlevelNumber = " + i);
            printGivenLevel(root, i);
        }
    }

    /* Print nodes at a given level */
    void printGivenLevel(Node node, int level)
    {
        if(node == null)
            return;
        if(level == 1){
            this.printPointArray(node.value);
            out.println("sortType = " + node.sortType);
            out.println("edgeCoordinate1 = " + node.edgeCoordMin);
            out.println("edgeCoordinate2 = "+node.edgeCoordMax);
        }
        else if (level > 1)
        {
            printGivenLevel(node.left, level-1);
            printGivenLevel(node.right, level-1);
        }
    }

    public void print(){
        this.printLevelOrder(rootNode);
    }

    public Node getBSPTree2DRootNode(){
        return this.rootNode;
    }

    public Point[] getBSPTree2DPointsArray(){
//        treeTraverse(this.rootNode);
        return sourcePoints;
    }

    public void drawBSPTreeJavaGUI(){
//        EventQueue.invokeLater(new Runnable() {  //Что это?
//            public void run() {
                PaintFrame frame = new PaintFrame(this );
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
//            }
//        });
    }

    private static void printPointArray(Point[] points){
        if (points != null){
            for (int i=0;i<points.length;i++){
                out.print("("+ points[i].coord[0]+","+ points[i].coord[1]+")");
            }
            out.println();
        }
    }

    public boolean findPointsInRectangle (Point[] rectanglePoints){
        return this.findPointsInRectanglePrivate(rectanglePoints, this.rootNode);
    }

    private boolean findPointsInRectanglePrivate(Point[] rectanglePoints, Node rootNode){
        int rectCoordNW = rectanglePoints[0].coord[rootNode.sortType];
        int rectCoordSE = rectanglePoints[1].coord[rootNode.sortType];
        if (this.rootNode.value == null){
            if (rectCoordNW <= this.rootNode.edgeCoordMin){
                if (rectCoordSE <= this.rootNode.edgeCoordMin){
                    findPointsInRectanglePrivate(rectanglePoints, rootNode.left);
                    //1 scenario
                }
                else{
                    if (rectCoordSE<= this.rootNode.edgeCoordMax) {
                        rectanglePoints[0].coord[rootNode.sortType] = this.rootNode.edgeCoordMin;
                        findPointsInRectanglePrivate(rectanglePoints, rootNode.right);
                        //5 scenario
                    }
                    else {
                        Point[] newRectPoints = new Point[2];
                        newRectPoints = rectanglePoints;
                        newRectPoints[1].coord[rootNode.sortType] = this.rootNode.edgeCoordMin;
                        findPointsInRectanglePrivate(newRectPoints, rootNode.left);
                        newRectPoints = rectanglePoints;
                        newRectPoints[0].coord[rootNode.sortType] = this.rootNode.edgeCoordMax;
                        findPointsInRectanglePrivate(newRectPoints, rootNode.right);
                        // 2 scenario
                    }
                }
            }
            else{
                if (rectCoordNW <= this.rootNode.edgeCoordMax){
                    if (rectCoordSE<= this.rootNode.edgeCoordMax){
                        return false; //no points in rectangle
                        // 4 scenario
                    }
                    else{
                        rectanglePoints[1].coord[rootNode.sortType] = this.rootNode.edgeCoordMax;
                        findPointsInRectanglePrivate(rectanglePoints, rootNode.right); // 6 scenario
                    }
                }
                else{
                    findPointsInRectanglePrivate(rectanglePoints, rootNode.right); //3 scenario
                }
            }
        }
        else{
            //standart search
            this.printPointArray (rootNode.value);
        }
    }
//    public Point[] searchPointsForRectangle{
//        return ;
//    }
}

