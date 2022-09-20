import SwiftUI
import shared

struct ContentView: View {
  @StateObject private var viewModel = ListViewModel()
	let lst = ["BTC", "USD"]

	var body: some View {
    NavigationView {
      List(viewModel.items, id: \.self) {item in
        NavigationLink(destination: DetailsView(symbol: item.code)) {
          HStack {
            Text(item.code).frame(maxWidth: .infinity, alignment: .leading)
            Text("\(item.value) \(viewModel.base)")
          }
        }
      }.onAppear{
        viewModel.refresh()
      }.navigationTitle("Currencies")
    }
  }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}

class ListViewModel: ObservableObject {
  private let popularSymbols = ["BTC", "ETH", "BNB", "XRP", "ADA", "SOL", "DOGE"]
  private let api = ServiceLocator.shared.exchangeRateApi
  @Published var items: [Rate] = []
  @Published var base: String = "USD"
  
  func refresh() {
    api.getRates(symbols: [], base: base) { rates, error in
      if let rates = rates {
        DispatchQueue.main.async {
          self.items = rates
        }
      }
    }
  }
}
