import SwiftUI
import Charts
import shared

struct DetailsView: View {
  @StateObject var viewModel = DetailsViewModel()
  let symbol: String
  
  var body: some View {
    VStack {
      Chart {
        ForEach(viewModel.timeSeries, id: \.self) { item in
          LineMark(x: .value("Date", "\(item.date)"), y: .value("value", item.value))
        }
      }.chartYScale(domain: viewModel.minYScale()...viewModel.maxYScale())
    }.onAppear{
      viewModel.refresh(symbol: self.symbol)
    }.navigationTitle(symbol)
  }
}

struct DetailsView_Previews: PreviewProvider {
  static var previews: some View {
    DetailsView(symbol: "BTC")
  }
}

class DetailsViewModel: ObservableObject {
  private let api = ServiceLocator.shared.exchangeRateApi
  @Published var timeSeries: [TimeSeries] = []
  
  func refresh(symbol: String) {
    let endDate = Date().addingTimeInterval(-3600 * 24)
    let startDate = endDate.addingTimeInterval(-3600 * 24 * 30) // 30 days
    let cal = Calendar.current
    let comps1 = cal.dateComponents([.year, .month, .day], from: startDate)
    let comps2 = cal.dateComponents([.year, .month, .day], from: endDate)
    
    let start: Kotlinx_datetimeLocalDate = Kotlinx_datetimeLocalDate(year: Int32(comps1.year!), monthNumber: Int32(comps1.month!), dayOfMonth: Int32(comps1.day!))
    let end: Kotlinx_datetimeLocalDate = Kotlinx_datetimeLocalDate(year: Int32(comps2.year!), monthNumber: Int32(comps2.month!), dayOfMonth: Int32(comps2.day!))
    
    api.getTimeSeries(start: start, end: end, symbol: symbol, base: "USD") { timeSeries, error in
      DispatchQueue.main.async {
        if let timeSeries = timeSeries {
          self.timeSeries = timeSeries
        }
      }
    }
  }
  
  func minYScale() -> Double {
    return timeSeries.min { obj1, obj2 in
      obj1.value < obj2.value
    }?.value ?? 0.0
  }
  
  func maxYScale() -> Double {
    return timeSeries.min { obj1, obj2 in
      obj1.value > obj2.value
    }?.value ?? 1.0
  }
  
  
}
