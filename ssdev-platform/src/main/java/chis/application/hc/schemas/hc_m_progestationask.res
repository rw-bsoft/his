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
					<!--['or',['eq',['$','a.createUser'],["$",'%user.properties.refUserId']],
					['eq',['$','a.createUser'],["$",'%user.userId']]]
					-->
					['like', ['$','c.regionCode'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>
			</conditions>
		</p>
		<p principal="post" mode="15">
			<conditions>			 
				<filter action="query">
					<!--['or',['eq',['$','a.createUser'],["$",'%user.properties.refUserId']],
					['eq',['$','a.createUser'],["$",'%user.userId']]]
					-->
					['like', ['$','c.regionCode'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>
			</conditions>
		</p>
		<p principal="T04" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','c.regionCode'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T05" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','c.regionCode'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T06" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','c.regionCode'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T13" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','c.regionCode'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T14" mode="15">
			<conditions>			 
				<filter action="query">
					['like', ['$','c.regionCode'], ['concat',['$','%user.regionCode'],['s','%']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T20" mode="15">
			<conditions>			 
				<filter action="query">
					['eq',['$','c.familyDoctorId'],["$",'%user.userId']]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>