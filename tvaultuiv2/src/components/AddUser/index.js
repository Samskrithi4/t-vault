/* eslint-disable no-nested-ternary */
import React, { useState, useEffect, useCallback } from 'react';
import { debounce } from 'lodash';
import { makeStyles } from '@material-ui/core/styles';
import { InputLabel, Typography } from '@material-ui/core';
import useMediaQuery from '@material-ui/core/useMediaQuery';
import styled, { css } from 'styled-components';
import PropTypes from 'prop-types';
import ComponentError from '../../errorBoundaries/ComponentError/component-error';
import mediaBreakpoints from '../../breakpoints';
import AutoCompleteComponent from '../FormFields/AutoComplete';
import ButtonComponent from '../FormFields/ActionButton';
import apiService from '../../views/private/safe/apiService';
import LoaderSpinner from '../Loaders/LoaderSpinner';
import RadioButtonComponent from '../FormFields/RadioButton';
import configData from '../../config/config';
import TextFieldComponent from '../FormFields/TextField';
import {
  InstructionText,
  RequiredCircle,
  RequiredText,
} from '../../styles/GlobalStyles';

const { small, smallAndMedium } = mediaBreakpoints;

const PermissionWrapper = styled.div`
  padding: 1rem 4rem 4rem 4rem;
  background-color: #1f232e;
  display: flex;
  flex-direction: column;
  margin-top: 2rem;
  ${small} {
    padding: 2.2rem 2.4rem 2.4rem 2.4rem;
  }
`;
const HeaderWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  div {
    display: flex;
    align-items: center;
  }
  .MuiTypography-h5 {
    font-weight: normal;
    ${small} {
      font-size: 1.6rem;
    }
  }
`;

const InputWrapper = styled.div`
  margin-top: 4rem;
  margin-bottom: 2.4rem;
  position: relative;
  .MuiInputLabel-root {
    display: flex;
    align-items: center;
  }
`;

const RadioButtonWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  ${smallAndMedium} {
    flex-direction: column;
  }
`;
const CancelSaveWrapper = styled.div`
  display: flex;
  ${smallAndMedium} {
    align-self: flex-end;
    margin-top: 3rem;
  }
`;

const CancelButton = styled.div`
  margin-right: 0.8rem;
  ${small} {
    width: 100%;
  }
`;

const customStyle = css`
  position: absolute;
  right: 1.2rem;
  top: 2.6rem;
  color: red;
`;

const useStyles = makeStyles(() => ({
  icon: {
    color: '#5e627c',
    fontSize: '2rem',
  },
}));

const AddUser = (props) => {
  const {
    handleCancelClick,
    handleSaveClick,
    username,
    access,
    isSvcAccount,
    isCertificate,
  } = props;
  const classes = useStyles();
  const [radioValue, setRadioValue] = useState('read');
  const [searchValue, setSearchValue] = useState('');
  const [options, setOptions] = useState([]);
  const [disabledSave, setDisabledSave] = useState(true);
  const [searchLoader, setSearchLoader] = useState(false);
  const [isValidUserName, setIsValidUserName] = useState(true);
  const isMobileScreen = useMediaQuery(small);

  useEffect(() => {
    setSearchValue(username);
    setRadioValue(access);
  }, [username, access]);

  useEffect(() => {
    if (searchValue?.length > 2) {
      if (!searchLoader) {
        if (options.length === 0 || !options.includes(searchValue)) {
          setIsValidUserName(false);
        } else {
          setIsValidUserName(true);
        }
      }
    }
  }, [searchValue, searchLoader, options]);

  useEffect(() => {
    if (configData.AD_USERS_AUTOCOMPLETE) {
      if (username) {
        if (
          (username.toLowerCase() !== searchValue?.toLowerCase() &&
            !isValidUserName) ||
          (username.toLowerCase() === searchValue?.toLowerCase() &&
            access === radioValue)
        ) {
          setDisabledSave(true);
        } else {
          setDisabledSave(false);
        }
      } else if (!isValidUserName || searchValue === '') {
        setDisabledSave(true);
      } else {
        setDisabledSave(false);
      }
    } else if (
      (username.toLowerCase() === searchValue?.toLowerCase() &&
        access === radioValue) ||
      searchValue === ''
    ) {
      setDisabledSave(true);
    } else {
      setDisabledSave(false);
    }
  }, [searchValue, radioValue, access, username, isValidUserName]);

  const handleChange = (event) => {
    setRadioValue(event.target.value);
  };

  const callSearchApi = useCallback(
    debounce(
      (value) => {
        setSearchLoader(true);
        apiService
          .getUserName(value)
          .then((res) => {
            setOptions([]);
            setSearchLoader(false);
            if (res?.data?.data?.values?.length > 0) {
              const array = [];
              res.data.data.values.map((item) => {
                if (item.userName) {
                  return array.push(item.userName.toLowerCase());
                }
                return null;
              });
              setOptions([...array]);
            }
          })
          .catch(() => {
            setSearchLoader(false);
          });
      },
      1000,
      true
    ),
    []
  );

  const onSearchChange = (e) => {
    if (e) {
      setSearchValue(e.target?.value);
      if (e.target?.value !== '' && e.target?.value?.length > 2) {
        callSearchApi(e.target.value);
      }
    }
  };

  const onSelected = (e, val) => {
    setSearchValue(val);
  };

  return (
    <ComponentError>
      <PermissionWrapper>
        <HeaderWrapper>
          <Typography variant="h5">Add User</Typography>
          <div>
            <RequiredCircle />
            <RequiredText>Required</RequiredText>
          </div>
        </HeaderWrapper>
        <InputWrapper>
          <InputLabel>
            User Name
            <RequiredCircle margin="0.5rem" />
          </InputLabel>
          {configData.AD_USERS_AUTOCOMPLETE ? (
            <>
              <AutoCompleteComponent
                options={options}
                icon="search"
                classes={classes}
                searchValue={searchValue}
                onSelected={(e, val) => onSelected(e, val)}
                onChange={(e) => onSearchChange(e)}
                placeholder="Username - Enter min 3 characters"
                error={username !== searchValue && !isValidUserName}
                helperText={
                  username !== searchValue && !isValidUserName
                    ? `User name ${searchValue} does not exist!`
                    : ''
                }
              />
              <InstructionText>
                Search the T-Mobile system to add users
              </InstructionText>
              {searchLoader && <LoaderSpinner customStyle={customStyle} />}
            </>
          ) : (
            <TextFieldComponent
              value={searchValue}
              placeholder="Username"
              fullWidth
              name="searchValue"
              onChange={(e) => setSearchValue(e.target.value)}
            />
          )}
        </InputWrapper>
        <RadioButtonWrapper>
          <RadioButtonComponent
            menu={
              isSvcAccount
                ? ['read', 'reset', 'deny']
                : isCertificate
                ? ['read', 'deny']
                : ['read', 'write', 'deny']
            }
            handleChange={(e) => handleChange(e)}
            value={radioValue}
          />
          <CancelSaveWrapper>
            <CancelButton>
              <ButtonComponent
                label="Cancel"
                color="primary"
                onClick={handleCancelClick}
                width={isMobileScreen ? '100%' : ''}
              />
            </CancelButton>
            <ButtonComponent
              label={username && access ? 'Edit' : 'Save'}
              color="secondary"
              onClick={() => handleSaveClick(searchValue, radioValue)}
              disabled={disabledSave}
              width={isMobileScreen ? '100%' : ''}
            />
          </CancelSaveWrapper>
        </RadioButtonWrapper>
      </PermissionWrapper>
    </ComponentError>
  );
};

AddUser.propTypes = {
  handleSaveClick: PropTypes.func.isRequired,
  handleCancelClick: PropTypes.func.isRequired,
  username: PropTypes.string,
  access: PropTypes.string,
  isSvcAccount: PropTypes.bool,
  isCertificate: PropTypes.bool,
};

AddUser.defaultProps = {
  username: '',
  access: 'read',
  isSvcAccount: false,
  isCertificate: false,
};

export default AddUser;