<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_ZFBL" alias="自负比例">
	<item id="SFXM" alias="收费项目" length="18" type="long" display="1" width="100" fixed="true" not-null="1" pkey="true">
		<dic id="phis.dictionary.chargesCollectable"/>
	</item>
	<item id="BRXZ" alias="病人性质" type="long" display="0" length="18" not-null="1" pkey="true"/>
	<item id="ZFBL" alias="自负比例%" type="double" precision="1" nullToValue="0" width="100"  max="100" min="0" length="6" not-null="1"/>
</entry>
