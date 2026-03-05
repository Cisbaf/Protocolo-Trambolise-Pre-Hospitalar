
import './App.css'
import { ProtocoloTromboliseForm } from './componentes/ProtocoloTromboliseForm'
import { GroupContextProvider } from './context/GroupContext'

function App() {

  return (
    <GroupContextProvider>
      <ProtocoloTromboliseForm/>
    </GroupContextProvider>
  )
}

export default App;
