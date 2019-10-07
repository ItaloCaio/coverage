package tees.ast.parser;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import tees.ast.parser.triangle.Triangle;
import tees.ast.parser.triangle.TriangleKind;
public class TriangleTest2 {
  @Test public void equilateralTriangleHaveEqualSides() throws Exception {
    System.out.println("Executando do metodo: equilateralTriangleHaveEqualSides");
    Triangle triangle=new Triangle(2,2,2);
    assertEquals(TriangleKind.EQUILATERAL,triangle.getKind());
  }
  @Test public void largerEquilateralTrianglesAlsoHaveEqualSides() throws Exception {
    System.out.println("Executando do metodo: largerEquilateralTrianglesAlsoHaveEqualSides");
    Triangle triangle=new Triangle(10,10,10);
    assertEquals(TriangleKind.EQUILATERAL,triangle.getKind());
  }
  @Test public void isoscelesTrianglesHaveLastTwoSidesEqual() throws Exception {
    System.out.println("Executando do metodo: isoscelesTrianglesHaveLastTwoSidesEqual");
    Triangle triangle=new Triangle(3,4,4);
    assertEquals(TriangleKind.ISOSCELES,triangle.getKind());
  }
  @Test public void isoscelesTrianglesHaveFirstAndLastSidesEqual() throws Exception {
    System.out.println("Executando do metodo: isoscelesTrianglesHaveFirstAndLastSidesEqual");
    Triangle triangle=new Triangle(4,3,4);
    assertEquals(TriangleKind.ISOSCELES,triangle.getKind());
  }
  @Test public void isoscelesTrianglesHaveTwoFirstSidesEqual() throws Exception {
    System.out.println("Executando do metodo: isoscelesTrianglesHaveTwoFirstSidesEqual");
    Triangle triangle=new Triangle(4,4,3);
    assertEquals(TriangleKind.ISOSCELES,triangle.getKind());
  }
  @Test public void isoscelesTrianglesHaveInFactExactlyTwoSidesEqual() throws Exception {
    System.out.println("Executando do metodo: isoscelesTrianglesHaveInFactExactlyTwoSidesEqual");
    Triangle triangle=new Triangle(10,10,2);
    assertEquals(TriangleKind.ISOSCELES,triangle.getKind());
  }
  @Test public void scaleneTrianglesHaveNoEqualSides() throws Exception {
    System.out.println("Executando do metodo: scaleneTrianglesHaveNoEqualSides");
    Triangle triangle=new Triangle(3,4,5);
    assertEquals(TriangleKind.SCALENE,triangle.getKind());
  }
  @Test public void scaleneTrianglesHaveNoEqualSidesAtLargerScaleEither() throws Exception {
    System.out.println("Executando do metodo: scaleneTrianglesHaveNoEqualSidesAtLargerScaleEither");
    Triangle triangle=new Triangle(10,11,12);
    assertEquals(TriangleKind.SCALENE,triangle.getKind());
  }
  @Test public void scaleneTrianglesHaveNoEqualSidesInDescendingOrderEither() throws Exception {
    System.out.println("Executando do metodo: scaleneTrianglesHaveNoEqualSidesInDescendingOrderEither");
    Triangle triangle=new Triangle(5,4,2);
    assertEquals(TriangleKind.SCALENE,triangle.getKind());
  }
  @Test public void verySmallTrianglesAreLegal() throws Exception {
    System.out.println("Executando do metodo: verySmallTrianglesAreLegal");
    Triangle triangle=new Triangle(0.4,0.6,0.3);
    assertEquals(TriangleKind.SCALENE,triangle.getKind());
  }
}