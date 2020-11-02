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
					['or',
					['eq',['$','a.sourceDoctor'],["$",'%user.properties.refUserId']],
					['eq',['$','a.targetDoctor'],["$",'%user.properties.refUserId']],
					['eq',['$','a.applyUser'],['$','%user.properties.refUserId']]]
				</filter>			
			</conditions>
		</p>
		<p principal="post" mode="15">
			<conditions>			 
				<filter action="query">
					['or',
					['eq',['$','a.sourceDoctor'],["$",'%user.properties.refUserId']],
					['eq',['$','a.targetDoctor'],["$",'%user.properties.refUserId']],
					['eq',['$','a.applyUser'],['$','%user.properties.refUserId']]]
				</filter>			
			</conditions>
		</p>
		<p principal="T04" mode="15">
			<conditions>			 
				<filter action="query">
					['or',
					['like', ['$','a.sourceUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.targetUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.applyUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]]
				</filter>			
			</conditions>
		</p>
		<p principal="T05" mode="15">
			<conditions>			 
				<filter action="query">
					['or',
					['like', ['$','a.sourceUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.targetUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.applyUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]]
				</filter>			
			</conditions>
		</p>
		<p principal="T14" mode="15">
			<conditions>			 
				<filter action="query">
					['or',
					['like', ['$','a.sourceUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.targetUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]],
					['like', ['$','a.applyUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>