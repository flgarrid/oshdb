package org.heigit.bigspatialdata.oshdb.tool.importer.util.idcell;

import java.io.Closeable;
import java.io.IOException;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

public interface IdToCellSource extends Closeable{

	
	public long get(long id) throws IOException;
	public LongSet get(LongSortedSet idss) throws IOException;
	
}