UPDATE KRIM_PERM_ATTR_DATA_T SET PERM_ID=375 WHERE ATTR_DATA_ID =572
/
UPDATE KRIM_ROLE_PERM_T SET PERM_ID=375 WHERE ROLE_PERM_ID=684
/
UPDATE KRIM_PERM_ATTR_DATA_T SET PERM_ID=376 WHERE ATTR_DATA_ID =573
/
UPDATE KRIM_ROLE_PERM_T SET PERM_ID=376 WHERE ROLE_PERM_ID=685
/
UPDATE KRIM_ROLE_PERM_T SET PERM_ID=377 WHERE ROLE_PERM_ID IN (686,687)
/

update krns_parm_t set TXT = 'F=A,C,S;G=A,C,S;N=A,C,S;P=A,C,S;S=A,C,S;T=A,C,S;A=N' where parm_nm = 'VALID_ASSET_STATUSES_BY_ACQUISITION_TYPE'
/
