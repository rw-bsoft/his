<?xml version="1.0" encoding="UTF-8"?>
<entity>
    <permissions>
        <p principal="$Others" mode="15">
            <conditions>
                <filter action="query">
<!--                        ['like',['$','a.JGID'], ['concat',['$','%user.manageUnit.id'],['s','%']]]-->
                    ['eq',['$','a.isBottom'],['s','y']]
                </filter>
            </conditions>
        </p>
    </permissions>
</entity>