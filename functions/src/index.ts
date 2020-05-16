import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
admin.initializeApp()

export const notificacaoAtualizada =  functions.firestore.document("marcacoes/{uuid}").onUpdate((snapshot, context) =>{
  const after = snapshot.after.data()
          if(after){
              if(after.estado === 'aceite'){
                  admin.firestore().doc(`pessoas/${after.uidPersonal}`).get()
                      .then(snap => {
                          const payload = {          
                              data: {
                                "tipoDeConta": "usuario",
                                "marcacaoID": `${after.marcacaoId}`
                              },
                              notification: {
                                "title": "marcação aceite",
                                "text": `${snap.get('nome')} aceitou a sua marcação, pode efectuar o pagamento`
                              }
                      }
                      return admin.messaging().sendToTopic(after.uidUsuario,payload)
                      }).catch(
                          error => {
                              console.log(error)
                          })
              }
              if(after.estado === 'paga'){
                  admin.firestore().doc(`pessoas/${after.uidUsuario}`).get()
                      .then(snap => {
                  const payload = {          
                      data: {
                        "tipoDeconta": "personal"
                      },
                      notification: {
                        "title": "marcação paga",
                        "text":`${snap.get('nome')} efetuou o pagamento da marcação a sua marcação, pode efectuar o pagamento, ver detalhes`
                      }
              }
              return admin.messaging().sendToTopic(after.uidPersonal,payload)
              }).catch(
                  error => {
                      console.log(error)
                  })
          }    
      }
              return;       
  })

  export const notificacaoCriada =  functions.firestore.document("marcacoes/{uuid}").onCreate((snapshot, context) =>{
      const marcacao = snapshot.data()
              if(marcacao){
                  const payload = {          
                      data: {
                        "tipoDeconta": "personal"
                      },
                      notification: {
                        "title": "Foi marcada uma sessão consigo",
                        "text": "Foi marcada uma sessão consigo, .",
                      }
              }
              return admin.messaging().sendToTopic(marcacao.uidPersonal,payload)
              }
                  return;
      })