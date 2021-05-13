/**
 * @format
 */

import {AppRegistry} from 'react-native';
import App from './App';
import {name as appName} from './app.json';
import AndroidIncomingCallHeadlessTask from './AndroidIncomingCallHeadlessTask';

AppRegistry.registerComponent(appName, () => App);
AppRegistry.registerComponent(
  'AndroidIncomingCall',
  () => require('./Ringer').default,
);

AppRegistry.registerHeadlessTask(
  'AndroidIncomingCallTask',
  () => AndroidIncomingCallHeadlessTask,
);
AppRegistry.registerHeadlessTask(
  'RNCallKeepBackgroundMessage',
  () =>
    ({name, callUUID, handle}) => {
      // Make your call here

      return Promise.resolve();
    },
);
