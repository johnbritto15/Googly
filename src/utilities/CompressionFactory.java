package utilities;

public class CompressionFactory {
	
public static Compression getCompressor(Compressors comp) 
	{
		switch (comp) 
		{
		case EMPTY:
			return new EmptyCompression();
		case VBYTE:
			return new VByteCompression();
		}
		return null;
	}
}
