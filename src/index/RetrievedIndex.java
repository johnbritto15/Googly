package index;

public class RetrievedIndex 
{
	long offset;
	long bytesWritten;
	int docTermFreq;
	int collectionTermFreq;
	public RetrievedIndex(long offset, long bytesWritten, int docTermFreq, int collectionTermFreq) 
	{
		this.offset = offset;
		this.bytesWritten = bytesWritten;
		this.docTermFreq = docTermFreq;
		this.collectionTermFreq = collectionTermFreq;
	}
		
};

