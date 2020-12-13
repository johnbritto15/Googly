package index;

public class DocStats {
	long collectionSize;
    double averageDocLength;
    int numDocs;
    public DocStats(long collectionSize, double averageDocLength, int numDocs)
	{
		this.collectionSize=collectionSize;
		this.averageDocLength=averageDocLength;
		this.numDocs=numDocs;
	}
}
