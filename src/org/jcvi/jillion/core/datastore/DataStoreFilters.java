/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.datastore;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * {@code DataStoreFilters} is a utility class
 * that contains factory methods to create several
 * different types of {@link DataStoreFilter}
 * objects.
 * @author dkatzel
 *
 */
public final class DataStoreFilters {

	private DataStoreFilters(){
		//can not instantiate
	}
	/**
	 * Get a {@link DataStoreFilter} instance
	 * whose {@link DataStoreFilter#accept(String)}
	 * always returns {@code true}.
	 * @return a {@link DataStoreFilter} instance;
	 * never null.  May return the same instance
	 * multiple times.
	 */
	public static DataStoreFilter alwaysAccept(){
		return DataStoreFilterSingletons.ALWAYS_ACCEPT;
	}
	/**
	 * Get a {@link DataStoreFilter} instance
	 * whose {@link DataStoreFilter#accept(String)}
	 * always returns {@code false}.
	 * @return a {@link DataStoreFilter} instance;
	 * never null.  May return the same instance
	 * multiple times.
	 */
	public static DataStoreFilter neverAccept(){
		return DataStoreFilterSingletons.NEVER_ACCEPT;
	}
	/**
	 * Create a new {@link DataStoreFilter} instance
	 * whose {@link DataStoreFilter#accept(String)}
	 * returns {@code true}
	 * if and only if the id is contained
	 * in the given collection of ids 
	 * (and {@code false} otherwise).
	 * 
	 * @param ids a collection of ids that should 
	 * be accepted by the new {@link DataStoreFilter};
	 * can not be null.
	 * @return a new instance; never null.
	 * @throws NullPointerException if ids is null.
	 */
	public static DataStoreFilter newIncludeFilter(Collection<String> ids){
		if(ids==null){
			throw new NullPointerException("ids can not be null");
		}
		return new IncludeDataStoreFilter(ids);
	}
	/**
	 * Create a new {@link DataStoreFilter} instance
	 * whose {@link DataStoreFilter#accept(String)}
	 * returns {@code false}
	 * if and only if the id is contained
	 * in the given collection of ids
	 * (and {@code true} otherwise).
	 * @param ids a collection of ids that should 
	 * be accepted by the new {@link DataStoreFilter};
	 * can not be null.
	 * @return a new instance; never null.
	 * @throws NullPointerException if ids is null.
	 */
	public static DataStoreFilter newExcludeFilter(Collection<String> ids){
		if(ids==null){
			throw new NullPointerException("ids can not be null");
		}
		return new ExcludeDataStoreFilter(ids);
	}
	/**
	 * Create a new {@link DataStoreFilter}
	 * that wraps the given {@link DataStoreFilter}
	 * and returns the opposite value
	 * of the wrapped {@link DataStoreFilter}.
	 * @param filter the {@link DataStoreFilter} to invert;
	 * can not be null.
	 * @return a new {@link DataStoreFilter}
	 * that returns {@code true} if the wrapped
	 * filter's {@link DataStoreFilter#accept(String)}
	 * returns {@code false} and vice versa.
	 */
	public static DataStoreFilter invertFilter(DataStoreFilter filter){
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		return new InverseDataStoreFilter(filter);
	}
	
	/**
	 * Create a new {@link DataStoreFilter} instance
	 * whose {@link DataStoreFilter#accept(String)}
	 * returns {@code true}
	 * if and only if the id matches the given 
	 * {@link Pattern} completely.
	 * The accept method of the returned
	 * filter should return identically values
	 * as returned by
	 * {@code pattern.matcher(id).matches();}.
	 * 
	 * @param pattern a {@link Pattern} that
	 * will be used to match ids;
	 * can not be null.
	 * @return a new instance; never null.
	 * @throws NullPointerException if pattern is null.
	 * 
	 * @see Matcher#matches()
	 */
	public static DataStoreFilter newMatchFilter(Pattern pattern){
		if(pattern ==null){
			throw new NullPointerException("pattern can not be null");
		}
		return new PatternDataStoreFilter(pattern);
	}
	
	/**
	 * Create a new {@link DataStoreFilter}
	 * that will only accept any ids that are contained
	 * in the given DataStore. 
	 * The DataStore must remain open for the entire lifetime
	 * of this DataStoreFilter; otherwise it will be
	 * kept
	 * @param datastore the DataStore to check to see if the ids are
	 * contained in; can not be null.
	 * 
	 * @implNote internally the accept test is done using {@link DataStore#contains(String)}.
	 * 
	 * @return a new DataStoreFilter object; will never be null.
	 * 
	 * @throws NullPointerException if datastore is null.
	 * @since 5.0
	 */
	public static DataStoreFilter containedInDataStore(DataStore<?> datastore){
		Objects.requireNonNull(datastore);
		return new ContainedInDataStoreFilter(datastore);
	}
	
	
	private static final class ContainedInDataStoreFilter implements DataStoreFilter{
		private final DataStore<?> delegate;

		
		@Override
		public boolean accept(String id) {
			try {
				return delegate.contains(id);
			} catch (DataStoreException e) {
				throw new IllegalStateException("error accessing datastore", e);
			}
		}


		public ContainedInDataStoreFilter(DataStore<?> delegate) {
			this.delegate = delegate;
		}
		
		
	}
	private static final class IncludeDataStoreFilter implements DataStoreFilter{

	    private final Collection<String> ids;

	    /**
	     * Create a DataStoreFilters.newIncludeFilter.
	     * @param ids this list of ids that should be accepted
	     * by this filter.
	     */
	    public IncludeDataStoreFilter(Collection<String> ids) {
	        this.ids = ids;
	    }

	    @Override
	    public boolean accept(String id) {
	        return ids.contains(id);
	    }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ids.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof IncludeDataStoreFilter)) {
				return false;
			}
			IncludeDataStoreFilter other = (IncludeDataStoreFilter) obj;
			if (!ids.equals(other.ids)) {
				return false;
			}
			return true;
		}
	    
	    
	}
	
	private static final class ExcludeDataStoreFilter implements DataStoreFilter{

	    private final Collection<String> ids;

	    /**
	     * Create a DataStoreFilters.newIncludeFilter.
	     * @param ids this list of ids that should be accepted
	     * by this filter.
	     */
	    public ExcludeDataStoreFilter(Collection<String> ids) {
	        this.ids = ids;
	    }

	    @Override
	    public boolean accept(String id) {
	        return !ids.contains(id);
	    }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ids.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof ExcludeDataStoreFilter)) {
				return false;
			}
			ExcludeDataStoreFilter other = (ExcludeDataStoreFilter) obj;
			if (!ids.equals(other.ids)) {
				return false;
			}
			return true;
		}
	    
	    
	}
	
	private static final class InverseDataStoreFilter implements DataStoreFilter{

	    private final DataStoreFilter filter;
	    
	    
	    /**
	     * Create a new InverseDataStoreFilter which returns
	     * the oppoiste answers as the given filter.
	     * @param filter the fitler to invert.
	     */
	    public  InverseDataStoreFilter(DataStoreFilter filter) {
	        this.filter = filter;
	    }


	    /**
	     * Accepts the opposite of what the wrapped
	     * filter would have accepted.
	     * @return {@code true} if the wrapped filter
	     * would have returned {@code false}; {@code false} if the wrapped filter
	     * would have returned {@code true}.
	     */
	    @Override
	    public boolean accept(String id) {
	        return !filter.accept(id);
	    }
	}
	
	private enum DataStoreFilterSingletons implements DataStoreFilter{

	    ALWAYS_ACCEPT{
	    	/**
	 	    * Every id is always accepted.
	 	    * @return {@code true}.
	 	    */
	 	    @Override
	 	    public boolean accept(String id) {
	 	        return true;
	 	    }
	    },

	    NEVER_ACCEPT{
	    	/**
	 	    * No id is ever accepted.
	 	    * @return {@code false}.
	 	    */
	 	    @Override
	 	    public boolean accept(String id) {
	 	        return false;
	 	    }
	    },
	    ;
	}

	private static class PatternDataStoreFilter implements DataStoreFilter{

		private final Pattern pattern;
		
		public PatternDataStoreFilter(Pattern pattern) {
			this.pattern = pattern;
		}

		@Override
		public boolean accept(String id) {
			Matcher matcher =pattern.matcher(id);
			return matcher.matches();
		}
		
	}
}
