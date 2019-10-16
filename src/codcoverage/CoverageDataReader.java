package codcoverage;

import java.util.ArrayList;
import java.util.List;

public class CoverageDataReader {
	
	private List<String> expectedCoverage = new ArrayList<String>();
	private List<String> coverableLines = new ArrayList<String>();
	
	public List<String> getExpectedCoverage(){
		
		return expectedCoverage;
	}
	
	public List<String> getCoverableLines(){
		return coverableLines;
		
	}

	public void setExpectedCoverage(List<String> expectedCoverage) {
		this.expectedCoverage = expectedCoverage;
	}

	public void setCoverableLines(List<String> coverableLines) {
		this.coverableLines = coverableLines;
	}
	

}
