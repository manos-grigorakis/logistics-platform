import { provideCharts } from 'ng2-charts';
import {
  ArcElement,
  BarController,
  BarElement,
  CategoryScale,
  Colors,
  Legend,
  LinearScale,
  PieController,
  Tooltip
} from 'chart.js';

export const appChartsProvider = provideCharts({
  registerables: [
    BarController,
    BarElement,
    PieController,
    ArcElement,
    CategoryScale,
    LinearScale,
    Legend,
    Colors,
    Tooltip,
  ],
});
