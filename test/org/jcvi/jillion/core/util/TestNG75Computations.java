package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.jcvi.jillion.core.util.GenomeStatistics.GenomeStatisticsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestNG75Computations {

	
	@Parameters
	public static List<Object[]> params(){
		return Arrays.asList(
					new Object[]{ 80, 100, new int[]{80,70,50,40,30,20}},
					new Object[]{ 80, 100, new int[]{80,70,50,40,30,20, 10, 5}}
				
				);
	}
	
	
	private final int expectedAnswer;
	private final int genomeLength;
	
	private final int[] values;
	
	
	public TestNG75Computations(int expectedAnswer, int genomeLength, int[] values) {
		this.expectedAnswer = expectedAnswer;
		this.genomeLength = genomeLength;
		this.values = values;
	}
	
	
	
	
	@Test
	public void intBuilder(){
		GenomeStatisticsBuilder builder = GenomeStatistics.ng75Builder(genomeLength);
		for(int i : values){
			builder.add(i);
		}
		
		assertEquals(expectedAnswer, builder.build().getAsInt());
	}
	
	@Test
	public void longBuilder(){
		GenomeStatisticsBuilder builder = GenomeStatistics.ng75Builder(genomeLength);
		for(int i : values){
			builder.add( (long) i);
		}
		
		assertEquals(expectedAnswer, builder.build().getAsInt());
	}
	
	@Test
	public void intStream(){
		IntStream stream = IntStream.of(values);
		
		assertEquals(expectedAnswer, GenomeStatistics.ng75(stream, genomeLength).getAsInt());
	}
	
	@Test
	public void intXStream(){
		IntStream stream = IntStream.of(values);
		
		assertEquals(expectedAnswer, GenomeStatistics.ngX(stream, .75D, genomeLength).getAsInt());
	}
	
	@Test
	public void parallelIntStream(){
		IntStream stream = IntStream.of(values).parallel();
		
		assertEquals(expectedAnswer, GenomeStatistics.ng75(stream,genomeLength).getAsInt());
	}
	
	@Test
	public void longStream(){
		LongStream stream = IntStream.of(values).asLongStream();
		
		assertEquals(expectedAnswer, GenomeStatistics.ng75(stream, genomeLength).getAsInt());
	}
	
	@Test
	public void longXStream(){
		LongStream stream = IntStream.of(values).asLongStream();
		
		assertEquals(expectedAnswer, GenomeStatistics.ngX(stream,  .75D, genomeLength).getAsInt());
	}
	
	@Test
	public void IntegerStream(){
		int actual = IntStream.of(values)
									.mapToObj(Integer::valueOf)
									.collect(GenomeStatistics.ng75Collector(genomeLength))
									.getAsInt();
		
		assertEquals(expectedAnswer, actual);
	}
	
	@Test
	public void LongStream(){
		int actual = IntStream.of(values)
									.mapToObj(Long::valueOf)
									.collect(GenomeStatistics.ng75Collector(genomeLength))
									.getAsInt();
		
		assertEquals(expectedAnswer, actual);
	}
	
	@Test
	public void XStream(){
		int actual = IntStream.of(values)
									.mapToObj(Long::valueOf)
									.collect(GenomeStatistics.ngXCollector(genomeLength, .75D))
									.getAsInt();
		
		assertEquals(expectedAnswer, actual);
	}
	
}