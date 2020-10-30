In order authentication with DocStore to work you need:

1. spid_S112331_T.credential.properties,spid_S112331_T.p12,spid_S112331_T.p12.pwd -DvSZ version in your etc directory of TipD.
In my case : C:\data\Dev\Tip90\cs\pki\etc

2. In your current etc project folder keep spid_S112331_T.keystore.properties(DvSZ) file

3. Set Enviromental variables in comand line:
SET TIPD_HOME=C:\data\Dev\Tip90
SET ONEPKI_CORE_CONFIG_DIR=%TIPD_HOME%\cs\pki\api\config
SET ONEPKI_CORE_CREDENTIAL_DIR=%TIPD_HOME%\cs\pki\etc

or by running script before starting the program  called setenv.cmd
