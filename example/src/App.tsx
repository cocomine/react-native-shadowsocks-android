import { useState } from 'react';
import { Button, SafeAreaView, StatusBar, useColorScheme } from 'react-native';

import { Colors } from 'react-native/Libraries/NewAppScreen';
import { Shadowsocks } from 'react-native-shadowsocks-android';

export default function App() {
  const isDarkMode = useColorScheme() === 'dark';
  const [, setProfilesIDs] = useState<number[]>([]);

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <Button title={'Connect'} onPress={() => Shadowsocks.connect()} />
      <Button title={'Disconnect'} onPress={() => Shadowsocks.disconnect()} />
      <Button
        title={'Add Profile'}
        onPress={() => {
          const a = Shadowsocks.addProfile(
            'ss://eGNoYWNoYTIwLWlldGYtcG9seTEzMDU6QXhlN0oyYWtHIUBvQmdU@us.vpn.cocomine.cc:6373/#US_vpn'
          );
          setProfilesIDs(a);
        }}
      />
      <Button
        title={'Delete Profile'}
        onPress={() => Shadowsocks.deleteProfile(1)}
      />
      <Button
        title={'Clear Profiles'}
        onPress={() => Shadowsocks.clearProfiles()}
      />
      <Button
        title={'Switch Profile'}
        onPress={() => Shadowsocks.switchProfile(1)}
      />
    </SafeAreaView>
  );
}
