package chis.source.ehrview;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PersonInfoIdLoader extends AbstractIdLoader {

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#hasStatus()
	 */
	@Override
	public boolean hasStatus() {
		return true;
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getEntryName()
	 */
	@Override
	public String getEntryName() {
		return "PersonInfo";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getEntityName()
	 */
	@Override
	public String getEntityName() {
		return PersonInfo;
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdColumn()
	 */
	@Override
	public String getIdColumn() {
		return "id";
	}

	/* (non-Javadoc)
	 * @see chis.source.ehrview.IdLoader#getIdName()
	 */
	@Override
	public String getIdName() {
		return "PersonInfo.id";
	}

}