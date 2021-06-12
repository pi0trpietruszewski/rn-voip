import 'react-native-gesture-handler';
import * as React from 'react';
import {NavigationContainer, RouteProp} from '@react-navigation/native';
import {
  createStackNavigator,
  StackNavigationProp,
} from '@react-navigation/stack';
import Ringer from './Ringer';
import CallScreen from './CallScreen';

type CallDataType = {
  callUuid: string;
  roomName: string;
  username: string;
  onlyAudio: boolean;
  roomSid: string;
};

export type NavigationParamsType = CallStackParamList;
export type AndroidCallStackProps<T extends keyof NavigationParamsType> = {
  navigation: StackNavigationProp<NavigationParamsType, T>;
  route: RouteProp<NavigationParamsType, T>;
};

type CallStackParamList = {
  Ringer: CallDataType;
  Call: CallDataType & {
    isIncoming?: boolean;
    androidFullNotification?: boolean;
  };
};
const Stack = createStackNavigator<CallStackParamList>();
const AndroidCallActivity = (props: CallStackParamList['Ringer']) => {
  return (
    <NavigationContainer>
      <Stack.Navigator headerMode={'none'}>
        <Stack.Screen initialParams={props} name="Ringer" component={Ringer} />
        <Stack.Screen name="Call" component={CallScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default AndroidCallActivity;
