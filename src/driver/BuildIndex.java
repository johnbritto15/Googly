package driver;
import index.IndexBuilder;

public class BuildIndex {
	public static void main(String[] args)
	{
		String sourceFile=args[0];
		boolean compress = Boolean.parseBoolean(args[1]);
		IndexBuilder builder = new IndexBuilder();
		builder.buildIndex(sourceFile, compress);
	}
}
