import React from "react";
import useAvcDataList from "../hooks/useAvcList";



interface AvcSelectedType {
    useAvc: ReturnType<typeof useAvcDataList>;
}

const AvcSelectedContext = React.createContext<AvcSelectedType | null>(null);


interface AvcSelectedProps {
    children: any;
}

export function AvcSelectedProvider({children}: AvcSelectedProps) {
    const useAvc = useAvcDataList();

    return (
        <AvcSelectedContext.Provider value={{useAvc}}>
            {children}
        </AvcSelectedContext.Provider>
    )
    
}

export function useAvcSelectedContext() {
  const context = React.useContext(AvcSelectedContext)

  if (!context) {
    throw new Error(
      "AvcSelectedContext must be used inside AvcSelectedProvider"
    )
  }

  return context;
}