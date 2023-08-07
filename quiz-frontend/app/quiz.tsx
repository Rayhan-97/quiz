import { Button, StyleSheet } from "react-native";

import { Text, View } from "../components/Themed";
import { useEffect, useState } from "react";

const HEALTH_CHECK_ENDPOINT = "http://localhost:8080/actuator/health";

export default function QuizScreen() {
  const [healthy, setHealthy] = useState(false);

  useEffect(() => {
    const healthCheck = async () => {
      const response = await fetch(HEALTH_CHECK_ENDPOINT);

      if (response.ok) {
        setHealthy(true);
      }
    };

    healthCheck().catch(() => console.error("Backend not healthy!!"));
  }, []);

  return (
    <>
      {!healthy && <NotHealthy />}
      {healthy && <StartQuiz />}
    </>
  );
}

function NotHealthy() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Oh no, server might be down!</Text>
      <View
        style={styles.separator}
        lightColor="#eee"
        darkColor="rgba(255,255,255,0.1)"
      />
    </View>
  );
}

function StartQuiz() {
  return (
    <View style={styles.container}>
      <Button title="Start quiz" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
  },
  separator: {
    marginVertical: 30,
    height: 1,
    width: "80%",
  },
});
