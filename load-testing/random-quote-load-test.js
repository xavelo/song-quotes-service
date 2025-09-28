import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const ENDPOINT_URL = __ENV.ENDPOINT_URL || 'http://192.168.1.139:30015/api/quote/random';
const REQUEST_RATE = new Rate('random_quote_request_rate');
const REQUEST_DURATION = new Trend('random_quote_request_duration');

export const options = {
  vus: Number(__ENV.VUS || 5),
  duration: __ENV.DURATION || '1m',
  thresholds: {
    http_req_failed: [
      'rate<0.01',
    ],
    http_req_duration: [
      'p(95)<500',
    ],
    random_quote_request_rate: [
      'rate>0.99',
    ],
    random_quote_request_duration: [
      'p(95)<500',
    ],
  },
};

export default function () {
  const response = http.get(ENDPOINT_URL);

  const isSuccessful = check(response, {
    'status is 200 or 404': (res) => res.status === 200 || res.status === 404,
  });

  REQUEST_RATE.add(isSuccessful);
  REQUEST_DURATION.add(response.timings.duration);

  sleep(Number(__ENV.SLEEP || 1));
}
