#!/bin/bash

#test POST /api/14/projects
#using API v14, no xml result wrapper

# use api V14
API_VERSION=14
API_XML_NO_WRAPPER=true

DIR=$(cd `dirname $0` && pwd)
source $DIR/include.sh

# now submit req
runurl="${APIURL}/projects"

echo "TEST: POST /api/14/projects"

test_proj="APICreateTest"

cat > $DIR/proj_create.post <<END
{
    "name":"$test_proj",
    "description":"test1",
    "config":{
        "test.property":"test value"
    }
}
</project>
END

# post
docurl -X POST -D $DIR/headers.out --data-binary @$DIR/proj_create.post -H Content-Type:application/json ${runurl}?${params} > $DIR/curl.out
if [ 0 != $? ] ; then
    errorMsg "ERROR: failed POST request"
    exit 2
fi
assert_http_status 201 $DIR/headers.out


API_XML_NO_WRAPPER=true $SHELL $SRC_DIR/api-test-success.sh $DIR/curl.out || exit 2

#Check result
assert_json_value "$test_proj" ".name" $DIR/curl.out
assert_json_value "test value" ".config.\"test.property\"" $DIR/curl.out

echo "OK"

echo "TEST: POST /api/14/projects (existing project results in conflict)"

# post xml
docurl -X POST -D $DIR/headers.out --data-binary @$DIR/proj_create.post -H Content-Type:application/json ${runurl}?${params} > $DIR/curl.out
if [ 0 != $? ] ; then
    errorMsg "ERROR: failed POST request"
    exit 2
fi
assert_http_status 409 $DIR/headers.out

echo "OK"

# now delete the test project

runurl="${APIURL}/project/$test_proj"
docurl -X DELETE  ${runurl} > $DIR/curl.out
if [ 0 != $? ] ; then
    errorMsg "ERROR: failed DELETE request"
    exit 2
fi

rm $DIR/proj_create.post
rm $DIR/curl.out

