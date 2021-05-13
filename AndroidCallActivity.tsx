import 'react-native-gesture-handler';
import * as React from 'react';
import {NavigationContainer, RouteProp} from '@react-navigation/native';
import {
  createStackNavigator,
  StackNavigationProp,
} from '@react-navigation/stack';
import Ringer from './Ringer';

export type NavigationParamsType = CallStackParamList;
export type AndroidCallStackProps<T extends keyof NavigationParamsType> = {
  navigation: StackNavigationProp<NavigationParamsType, T>;
  route: RouteProp<NavigationParamsType, T>;
};

type CallStackParamList = {
  Ringer: {
    callUuid: string;
    roomName: string;
    username: string;
    onlyAudio: boolean;
    roomSid: string;
  };
};
const Stack = createStackNavigator<CallStackParamList>();
const AndroidCallActivity = (props: CallStackParamList['Ringer']) => {
  return (
    <NavigationContainer>
      <Stack.Navigator headerMode={'none'}>
        <Stack.Screen initialParams={props} name="Ringer" component={Ringer} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default AndroidCallActivity;
