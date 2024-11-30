package com.example.alarmoment_compose

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.example.alarmoment_compose.ui.theme.AlarMOMENT_composeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AlarMOMENT_composeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Spacer(modifier = Modifier.height(200.dp))
            TitlePage(
              text = "This is the Moment.",
              modifier = Modifier.padding(bottom = 64.dp)
            )
            SetAlarmButton( modifier = Modifier.padding(bottom = 32.dp) )
          }
        }
      }
    }
  }
}


@Composable
fun TitlePage(text: String, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Image(
      painter = painterResource(id = R.drawable.logo),
      contentDescription = stringResource(id = R.string.logo_desc),
      modifier = Modifier
        .padding(bottom = 16.dp)
        .size(200.dp)
    )
    Text(
      text = text,
      modifier = Modifier.padding(top = 16.dp)
    )
  }
}

@Composable
fun SetAlarmButton(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  
  val timeRemaining = remember { mutableIntStateOf(0) }
  val isTimerRunning = timeRemaining.intValue > 0
  val scope = rememberCoroutineScope()
  
  val notifID = 1
  val channelID = "alarmoment_channel"
  
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val channel = NotificationChannel(
      channelID,
      "Alarmoment Alert",
      NotificationManager.IMPORTANCE_HIGH
    ).apply {
      description = "ALARMOMENT IS DONE"
      enableVibration(true)
    }
    val notificationManager = context
      .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }
  
  fun sendNotification() {
    val intent = Intent(context, MainActivity::class.java).apply {
      putExtra("alarMoment momented", true)
    }
    
    val pendingIntent = android.app.PendingIntent.getActivity(
      context, 0, intent,
      android.app.PendingIntent.FLAG_UPDATE_CURRENT
    )
    
    val builder = NotificationCompat.Builder(context, channelID)
      .setSmallIcon(R.drawable.logo)
      .setContentTitle("ALARMOMENT")
      .setContentText("ALARMOMENT IS DONE")
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
    
    with(NotificationManagerCompat.from(context)) {
      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        notify(notifID, builder.build())
      }
    }
  }
  
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    if(isTimerRunning){
      Text(
        text = timeRemaining.intValue.toString(),
        style = MaterialTheme.typography.displayLarge
      )
    }
    
    FilledTonalButton(
      onClick = {
        Toast.makeText(
          context,
          "AlarMoment set in 1 minute.",
          Toast.LENGTH_SHORT
        ).show()
        
        timeRemaining.intValue = 60
        scope.launch {
          while (timeRemaining.intValue > 0) {
            delay(1000)
            timeRemaining.intValue--
          }
          sendNotification()
        }
        
      },
      enabled = !isTimerRunning,
      modifier = modifier
    ) { Text(text = if (isTimerRunning) "AlarMoment in Progress" else "Set AlarMoment") }
  }
}