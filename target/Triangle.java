package tees.ast.parser.triangle;
import java.util.HashSet;
import java.util.Set;
public class Triangle {
  private double side1;
  private double side2;
  private double side3;
  public Triangle(  double side1,  double side2,  double side3) throws TriangleException {
    System.out.println("Executando do metodo: Triangle");
    this.side1=side1;
    this.side2=side2;
    this.side3=side3;
    if (allSidesAreZero() || hasImpossibleSides() || violatesTriangleInequality()) {
      throw new TriangleException();
    }
  }
  public TriangleKind getKind(){
    System.out.println("Executando do metodo: getKind");
    int uniqueSides=getNumberOfUniqueSides();
    if (uniqueSides == 1) {
      return TriangleKind.EQUILATERAL;
    }
    if (uniqueSides == 2) {
      return TriangleKind.ISOSCELES;
    }
    return TriangleKind.SCALENE;
  }
  private boolean allSidesAreZero(){
    System.out.println("Executando do metodo: allSidesAreZero");
    return side1 == 0 && side2 == 0 && side3 == 0;
  }
  private boolean hasImpossibleSides(){
    System.out.println("Executando do metodo: hasImpossibleSides");
    return side1 < 0 || side2 < 0 || side3 < 0;
  }
  private boolean violatesTriangleInequality(){
    System.out.println("Executando do metodo: violatesTriangleInequality");
    return side1 + side2 <= side3 || side1 + side3 <= side2 || side2 + side3 <= side1;
  }
  public int getNumberOfUniqueSides(){
  }
}
