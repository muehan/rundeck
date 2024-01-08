#!/bin/bash

#/ Purpose:
#/   Test the scm plugins api setup methods
#/ 

SRC_DIR=$(cd `dirname $0` && pwd)
DIR=${TMP_DIR:-$SRC_DIR}
export API_XML_NO_WRAPPER=1
source $SRC_DIR/include_scm_test.sh

ARGS=$@


proj="test"


test_setup_export_json_invalid_config(){
	local integration=$1
	local plugin=$2
	local dirval=$3
	local urlval=$4
	local msg=$5

	ENDPOINT="${APIURL}/project/$proj/scm/$integration/plugin/$plugin/setup"
	TMPDIR=`tmpdir`
	tmp=$TMPDIR/test_setup_export_xml-upload.json
	cat >$tmp <<END
{
	"config":{
		"dir":"$dirval",
		"url":"$urlval"
	}
}
END
	METHOD=POST
	ACCEPT=application/json
	TYPE=application/json
	POSTFILE=$tmp
	EXPECT_STATUS=400

	test_begin "Setup SCM Export: JSON: $msg"
	
	api_request $ENDPOINT $DIR/curl.out

	assert_json_value "false" '.success' $DIR/curl.out
	assert_json_value "Some input values were not valid." '.message' $DIR/curl.out
	if [ -z "$dirval" ] ; then
		assert_json_value "required" '.validationErrors.dir' $DIR/curl.out
	else
		assert_json_null '.validationErrors.dir' $DIR/curl.out
	fi
	if [ -z "$urlval" ] ; then
		assert_json_value "required" '.validationErrors.url' $DIR/curl.out
	else
		assert_json_null '.validationErrors.url' $DIR/curl.out
	fi

	test_succeed
}

test_setup_export_json_valid(){
	local integration=$1
	local plugin=$2
	local project=$3
	ENDPOINT="${APIURL}/project/$project/scm/$integration/plugin/$plugin/setup"
	test_begin "Setup SCM Export: JSON"
	do_setup_export_json_valid $integration $plugin $project
	test_succeed
}


disable_plugin_json(){
	local integration=$1
	local plugin=$2
	local project=$3

	METHOD=POST
	ACCEPT=application/json
	ENDPOINT="${APIURL}/project/$project/scm/$integration/plugin/$plugin/disable"

	api_request $ENDPOINT $DIR/curl.out

	assert_json_value "true" '.success' $DIR/curl.out
}
enable_plugin_json(){
	local integration=$1
	local plugin=$2
	local project=$3

	METHOD=POST
	ACCEPT=application/json
	ENDPOINT="${APIURL}/project/$project/scm/$integration/plugin/$plugin/enable"

	api_request $ENDPOINT $DIR/curl.out

	assert_json_value "true" '.success' $DIR/curl.out
}
assert_plugin_enabled(){
	local integration=$1
	local plugin=$2
	local value=$3
	local project=$4

	ENDPOINT="${APIURL}/project/$project/scm/$integration/plugins"
	ACCEPT=application/json

	api_request $ENDPOINT $DIR/curl.out

	$SHELL $SRC_DIR/api-test-success.sh $DIR/curl.out || exit 2

	#Check projects list
	assert_json_value $integration '.integration' $DIR/curl.out

	assert_json_value "$value" ".plugins[] | select(.type == \"$plugin\") | .enabled" $DIR/curl.out
}

test_disable_export_json(){
	local project=$1
	

	do_setup_export_json_valid "export" "git-export" $project

	assert_plugin_enabled "export" "git-export" "true" $project
	
	ENDPOINT="${APIURL}/project/$project/scm/export/plugin/git-export/disable"
	test_begin "Disable plugin JSON"
	
	disable_plugin_json "export" "git-export" $project
	
	assert_plugin_enabled "export" "git-export" "false" $project
	
	test_succeed
}
test_disable_export_wrong_json(){
	local project=$1
	

	do_setup_export_json_valid "export" "git-export" $project

	assert_plugin_enabled "export" "git-export" "true" $project
	
	ENDPOINT="${APIURL}/project/$project/scm/export/plugin/wrong-plugin/disable"
	test_begin "Disable plugin wrong type JSON"
	
	METHOD=POST
	ACCEPT=application/json
	EXPECT_STATUS=400
	
	api_request $ENDPOINT $DIR/curl.out

	assert_json_value "false" '.success' $DIR/curl.out
	
	assert_plugin_enabled "export" "git-export" "true" $project
	
	test_succeed
}
test_disable_export_nosetup_json(){
	local project=$1
	

	#do_setup_export_json_valid "export" "git-export" $project

	assert_plugin_enabled "export" "git-export" "false" $project
	
	ENDPOINT="${APIURL}/project/$project/scm/export/plugin/wrong-plugin/disable"
	test_begin "Disable plugin not enabled JSON"
	
	METHOD=POST
	ACCEPT=application/json
	EXPECT_STATUS=400
	
	api_request $ENDPOINT $DIR/curl.out

	assert_json_value "false" '.success' $DIR/curl.out
	
	assert_plugin_enabled "export" "git-export" "false" $project
	
	test_succeed
}
test_disable_enable_export_json(){
	local project=$1
	

	do_setup_export_json_valid "export" "git-export" $project

	assert_plugin_enabled "export" "git-export" "true" $project
	
	disable_plugin_json "export" "git-export" $project
	
	assert_plugin_enabled "export" "git-export" "false" $project
	

	ENDPOINT="${APIURL}/project/$project/scm/export/plugin/git-export/disable"
	test_begin "Enable plugin JSON"

	enable_plugin_json "export" "git-export" $project
	
	assert_plugin_enabled "export" "git-export" "true" $project
	
	test_succeed
}



main(){

	test_setup_export_json_invalid_config "export" "git-export" "" "" "two missing params"
	test_setup_export_json_invalid_config "export" "git-export" "" "abc" "dir missing"
	test_setup_export_json_invalid_config "export" "git-export" "abc" "" "url missing"


	# setup using json
	create_project "testscm2"
	test_setup_export_json_valid "export" "git-export" "testscm2"
	remove_project "testscm2"

	# disable using json
	create_project "testscm3"
	test_disable_export_json "testscm3"
	remove_project "testscm3"

	create_project "testscm3-2"
	test_disable_export_wrong_json "testscm3-2"
	remove_project "testscm3-2"

	create_project "testscm3-3"
	test_disable_export_nosetup_json "testscm3-3"
	remove_project "testscm3-3"

	# disable using json
	create_project "testscm5"
	test_disable_enable_export_json "testscm5"
	remove_project "testscm5"

}

main