<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 			
			</conditions>
		</p>
		<p principal="T01" mode="15">
			<conditions>			 
				<filter action="query">
					<!--['eq',['$','a.manaDoctorId'],['$','%user.properties.refUserId']]-->
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="post" mode="15">
			<conditions>			 
				<filter action="query">
					<!--['eq',['$','a.manaDoctorId'],['$','%user.properties.refUserId']]-->
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T04" mode="15">
			<conditions>			 
				<filter action="query">
					<!--['like', ['$','a.manaUnitId'], ['concat',['$','%user.manageUnit.id'],['s','%']]]-->
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T05" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T06" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T07" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T08" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T13" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T14" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T15" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T16" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','a.changeareagrid'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>